package com.legal.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultOrder;
import com.legal.platform.entity.IncomeRecord;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.entity.ServiceEvaluation;
import com.legal.platform.mapper.ConsultOrderMapper;
import com.legal.platform.mapper.IncomeRecordMapper;
import com.legal.platform.mapper.LawyerInfoMapper;
import com.legal.platform.mapper.ServiceEvaluationMapper;
import com.legal.platform.service.ConsultOrderService;
import com.legal.platform.service.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ConsultOrderServiceImpl extends ServiceImpl<ConsultOrderMapper, ConsultOrder> implements ConsultOrderService {

    @Autowired
    private LawyerInfoMapper lawyerInfoMapper;

    @Autowired
    private ServiceEvaluationMapper serviceEvaluationMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private UserWalletService userWalletService;

    private LawyerInfo findLawyerInfo(Long lawyerId) {
        if (lawyerId == null) {
            return null;
        }
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerInfo::getUserId, lawyerId);
        LawyerInfo lawyer = lawyerInfoMapper.selectOne(wrapper);
        if (lawyer != null) {
            return lawyer;
        }
        return lawyerInfoMapper.selectById(lawyerId);
    }

    private Set<Long> getPossibleLawyerIds(Long lawyerId) {
        Set<Long> lawyerIds = new HashSet<>();
        if (lawyerId == null) {
            return lawyerIds;
        }
        lawyerIds.add(lawyerId);

        LambdaQueryWrapper<LawyerInfo> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(LawyerInfo::getUserId, lawyerId);
        LawyerInfo lawyerByUserId = lawyerInfoMapper.selectOne(userWrapper);
        if (lawyerByUserId != null) {
            if (lawyerByUserId.getId() != null) {
                lawyerIds.add(lawyerByUserId.getId());
            }
            if (lawyerByUserId.getUserId() != null) {
                lawyerIds.add(lawyerByUserId.getUserId());
            }
        }

        LawyerInfo lawyerById = lawyerInfoMapper.selectById(lawyerId);
        if (lawyerById != null) {
            if (lawyerById.getId() != null) {
                lawyerIds.add(lawyerById.getId());
            }
            if (lawyerById.getUserId() != null) {
                lawyerIds.add(lawyerById.getUserId());
            }
        }
        return lawyerIds;
    }

    @Override
    public PageResult<ConsultOrder> getOrderList(Integer page, Integer size, Long userId, Long lawyerId, Integer status, Integer consultType) {
        LambdaQueryWrapper<ConsultOrder> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ConsultOrder::getUserId, userId);
        }
        if (lawyerId != null) {
            Set<Long> lawyerIds = getPossibleLawyerIds(lawyerId);
            wrapper.in(ConsultOrder::getLawyerId, lawyerIds);
        }
        if (status != null) {
            wrapper.eq(ConsultOrder::getStatus, status);
        }
        if (consultType != null) {
            wrapper.eq(ConsultOrder::getConsultType, consultType);
        }
        wrapper.orderByDesc(ConsultOrder::getCreateTime);
        IPage<ConsultOrder> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<ConsultOrder> getOrderDetail(Long id) {
        ConsultOrder order = this.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @Override
    @Transactional
    public Result<Boolean> createOrder(ConsultOrder order, Long userId) {
        LawyerInfo lawyer = findLawyerInfo(order.getLawyerId());
        if (lawyer == null || lawyer.getStatus() != 1) {
            return Result.error("律师不存在或未通过审核");
        }
        order.setOrderNo("CONSULT" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setLawyerId(lawyer.getUserId() != null ? lawyer.getUserId() : order.getLawyerId());
        order.setStatus(1);
        Integer consultType = order.getConsultType() == null ? 1 : order.getConsultType();
        order.setConsultType(consultType);
        if (consultType == 1) {
            order.setPrice(lawyer.getConsultPrice());
        } else if (consultType == 2) {
            order.setPrice(lawyer.getConsultPrice());
        } else {
            order.setPrice(lawyer.getVideoPrice());
        }
        if (order.getPrice() == null) {
            order.setPrice(BigDecimal.ZERO);
        }
        if (order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            Result<Boolean> payResult = userWalletService.escrowPay(
                    userId,
                    order.getPrice(),
                    order.getOrderNo(),
                    "咨询服务费",
                    order.getPayPassword()
            );
            if (payResult.getCode() != 200) {
                return Result.error(payResult.getMessage());
            }
            order.setPayMethod("balance");
        } else {
            order.setPayMethod("free");
        }
        order.setPayStatus(1);
        order.setPayTime(LocalDateTime.now());
        this.save(order);
        return Result.success("下单成功", true);
    }

    @Override
    public Result<Boolean> acceptOrder(Long id, Long lawyerId) {
        ConsultOrder order = this.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getLawyerId().equals(lawyerId)) {
            return Result.error("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            return Result.error("订单状态不正确");
        }
        if (order.getPayStatus() == null || order.getPayStatus() != 1) {
            return Result.error("订单未支付，无法接单");
        }
        order.setStatus(2);
        order.setStartTime(LocalDateTime.now());
        this.updateById(order);
        return Result.success("接单成功", true);
    }

    @Override
    public Result<Boolean> processOrder(Long id, String result) {
        ConsultOrder order = this.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 2) {
            return Result.error("订单状态不正确");
        }
        order.setStatus(3);
        order.setResult(result);
        this.updateById(order);
        return Result.success("处理成功", true);
    }

    @Override
    @Transactional
    public Result<Boolean> finishOrder(Long id) {
        ConsultOrder order = this.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            return Result.error("订单状态不允许完成");
        }
        if (order.getPayStatus() == null || order.getPayStatus() != 1) {
            return Result.error("订单未处于已支付托管状态，无法结算");
        }
        LawyerInfo lawyer = findLawyerInfo(order.getLawyerId());
        if (lawyer == null || lawyer.getUserId() == null) {
            return Result.error("律师钱包信息不存在，无法结算");
        }
        if (order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            Result<Boolean> settleResult = userWalletService.settleEscrow(
                    order.getUserId(),
                    lawyer.getUserId(),
                    order.getPrice(),
                    order.getOrderNo(),
                    "咨询服务收入"
            );
            if (settleResult.getCode() != 200) {
                return Result.error(settleResult.getMessage());
            }
        }
        order.setStatus(4);
        order.setFinishTime(LocalDateTime.now());
        order.setPayStatus(3);
        this.updateById(order);
        if (lawyer != null) {
            lawyer.setServiceCount((lawyer.getServiceCount() == null ? 0 : lawyer.getServiceCount()) + 1);
            lawyerInfoMapper.updateById(lawyer);
        }
        if (order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            IncomeRecord incomeRecord = new IncomeRecord();
            incomeRecord.setRecordNo("INCOME" + IdUtil.getSnowflakeNextIdStr());
            incomeRecord.setLawyerId(order.getLawyerId());
            incomeRecord.setOrderId(id);
            incomeRecord.setOrderType(1);
            incomeRecord.setOrderAmount(order.getPrice());
            incomeRecord.setCommissionRate(BigDecimal.ZERO);
            incomeRecord.setCommissionAmount(BigDecimal.ZERO);
            incomeRecord.setIncomeAmount(order.getPrice());
            incomeRecord.setStatus(2);
            incomeRecord.setSettleTime(LocalDateTime.now());
            incomeRecordMapper.insert(incomeRecord);
        }
        return Result.success("订单完成", true);
    }

    @Override
    @Transactional
    public Result<Boolean> cancelOrder(Long id, Long userId) {
        ConsultOrder order = this.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            return Result.error("订单已被接单或已完成，无法取消");
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 1
                && order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            Result<Boolean> refundResult = userWalletService.refundEscrow(
                    userId,
                    order.getPrice(),
                    order.getOrderNo(),
                    "咨询订单取消退款"
            );
            if (refundResult.getCode() != 200) {
                return Result.error(refundResult.getMessage());
            }
            order.setPayStatus(2);
        }
        order.setStatus(5);
        this.updateById(order);
        return Result.success("订单已取消", true);
    }

    @Override
    public Result<Boolean> evaluateOrder(Long id, Integer score, String content) {
        ConsultOrder order = this.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 4) {
            return Result.error("订单未完成，无法评价");
        }
        if (order.getEvaluateStatus() == 1) {
            return Result.error("订单已评价");
        }
        order.setEvaluateStatus(1);
        order.setEvaluateScore(score);
        order.setEvaluateContent(content);
        this.updateById(order);
        ServiceEvaluation evaluation = new ServiceEvaluation();
        evaluation.setOrderId(id);
        evaluation.setOrderType(1);
        evaluation.setUserId(order.getUserId());
        evaluation.setLawyerId(order.getLawyerId());
        evaluation.setProfessionalScore(score);
        evaluation.setResponseScore(score);
        evaluation.setAttitudeScore(score);
        evaluation.setTotalScore(score);
        evaluation.setContent(content);
        serviceEvaluationMapper.insert(evaluation);
        return Result.success("评价成功", true);
    }

    @Override
    public Result<Map<String, Object>> getOrderStatistics(Long userId, String role) {
        Map<String, Object> stats = new HashMap<>();
        LambdaQueryWrapper<ConsultOrder> wrapper = new LambdaQueryWrapper<>();
        if ("lawyer".equals(role)) {
            wrapper.eq(ConsultOrder::getLawyerId, userId);
        } else {
            wrapper.eq(ConsultOrder::getUserId, userId);
        }
        long total = this.count(wrapper);
        stats.put("total", total);
        wrapper = new LambdaQueryWrapper<>();
        if ("lawyer".equals(role)) {
            wrapper.eq(ConsultOrder::getLawyerId, userId);
        } else {
            wrapper.eq(ConsultOrder::getUserId, userId);
        }
        wrapper.eq(ConsultOrder::getStatus, 1);
        long pending = this.count(wrapper);
        stats.put("pending", pending);
        wrapper = new LambdaQueryWrapper<>();
        if ("lawyer".equals(role)) {
            wrapper.eq(ConsultOrder::getLawyerId, userId);
        } else {
            wrapper.eq(ConsultOrder::getUserId, userId);
        }
        wrapper.eq(ConsultOrder::getStatus, 4);
        long completed = this.count(wrapper);
        stats.put("completed", completed);
        return Result.success(stats);
    }
}
