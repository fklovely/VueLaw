package com.legal.platform.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.DocumentOrder;
import com.legal.platform.entity.IncomeRecord;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.mapper.DocumentOrderMapper;
import com.legal.platform.mapper.IncomeRecordMapper;
import com.legal.platform.mapper.LawyerInfoMapper;
import com.legal.platform.service.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/document-order")
public class DocumentOrderController {

    @Autowired
    private DocumentOrderMapper documentOrderMapper;

    @Autowired
    private LawyerInfoMapper lawyerInfoMapper;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    private Set<Long> getPossibleDocumentLawyerIds(Long userId) {
        Set<Long> lawyerIds = new HashSet<>();
        if (userId == null) {
            return lawyerIds;
        }
        lawyerIds.add(userId);

        LambdaQueryWrapper<LawyerInfo> lawyerWrapper = new LambdaQueryWrapper<>();
        lawyerWrapper.eq(LawyerInfo::getUserId, userId);
        LawyerInfo lawyer = lawyerInfoMapper.selectOne(lawyerWrapper);
        if (lawyer != null) {
            if (lawyer.getId() != null) {
                lawyerIds.add(lawyer.getId());
            }
            if (lawyer.getUserId() != null) {
                lawyerIds.add(lawyer.getUserId());
            }
        }
        return lawyerIds;
    }

    /*@GetMapping("/list")
    public Result<PageResult<DocumentOrder>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");

        LambdaQueryWrapper<DocumentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentOrder::getLawyerId, lawyerId);
        if (orderType != null) {
            wrapper.eq(DocumentOrder::getOrderType, orderType);
        }
        if (status != null) {
            wrapper.eq(DocumentOrder::getStatus, status);
        }
        wrapper.orderByDesc(DocumentOrder::getCreateTime);
        
        IPage<DocumentOrder> pageResult = documentOrderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(pageResult.getRecords(), pageResult.getTotal()));
    }*/
    @GetMapping("/list")
    public Result<PageResult<DocumentOrder>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {

        try {
            // 获取当前登录用户的ID（sys_user.id）
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("当前登录用户ID (sys_user.id): " + userId);

            if (userId == null) {
                return Result.error("用户未登录");
            }

            // 根据 userId 获取律师信息，得到 lawyer_info.id
            LambdaQueryWrapper<LawyerInfo> lawyerWrapper = new LambdaQueryWrapper<>();
            lawyerWrapper.eq(LawyerInfo::getUserId, userId);
            LawyerInfo lawyer = lawyerInfoMapper.selectOne(lawyerWrapper);

            System.out.println("查询到的律师信息: " + (lawyer != null ? lawyer.getId() + ", " + lawyer.getLawyerName() : "null"));

            if (lawyer == null) {
                // 该用户不是律师，返回空列表
                return Result.success(new PageResult<>(new ArrayList<>(), 0L));
            }

            Set<Long> lawyerIds = getPossibleDocumentLawyerIds(userId);
            System.out.println("文书订单可匹配律师ID: " + lawyerIds);

            // 查询文书订单
            LambdaQueryWrapper<DocumentOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(DocumentOrder::getLawyerId, lawyerIds);

            if (orderType != null) {
                wrapper.eq(DocumentOrder::getOrderType, orderType);
            }
            if (status != null) {
                wrapper.eq(DocumentOrder::getStatus, status);
            }
            wrapper.orderByDesc(DocumentOrder::getCreateTime);

            IPage<DocumentOrder> pageResult = documentOrderMapper.selectPage(new Page<>(page, size), wrapper);

            System.out.println("查询到文书订单数量: " + pageResult.getRecords().size());

            return Result.success(new PageResult<>(pageResult.getRecords(), pageResult.getTotal()));

        } catch (Exception e) {
            System.err.println("查询文书订单失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }


    /*@GetMapping("/user-list")
    public Result<PageResult<DocumentOrder>> userList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        LambdaQueryWrapper<DocumentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentOrder::getUserId, userId);
        if (status != null) {
            wrapper.eq(DocumentOrder::getStatus, status);
        }
        wrapper.orderByDesc(DocumentOrder::getCreateTime);
        
        IPage<DocumentOrder> pageResult = documentOrderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(pageResult.getRecords(), pageResult.getTotal()));
    }*/
    @GetMapping("/user-list")
    public Result<PageResult<DocumentOrder>> userList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {

        // 获取当前登录用户ID和角色
        Long currentUserId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        LambdaQueryWrapper<DocumentOrder> wrapper = new LambdaQueryWrapper<>();

        // 关键修改：管理员可以查看所有订单，普通用户只能查看自己的订单
        if (!"admin".equals(role)) {
            wrapper.eq(DocumentOrder::getUserId, currentUserId);
        }

        if (status != null) {
            wrapper.eq(DocumentOrder::getStatus, status);
        }
        wrapper.orderByDesc(DocumentOrder::getCreateTime);

        IPage<DocumentOrder> pageResult = documentOrderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(pageResult.getRecords(), pageResult.getTotal()));
    }

    @PostMapping("/create")
    @Transactional
    public Result<Boolean> create(@RequestBody DocumentOrder order, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        LawyerInfo lawyer = lawyerInfoMapper.selectById(order.getLawyerId());
        if (lawyer == null || lawyer.getStatus() != 1) {
            return Result.error("律师不存在或未通过审核");
        }
        
        order.setOrderNo("DOC" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setStatus(1);

        Integer orderType = order.getOrderType() == null ? 1 : order.getOrderType();
        order.setOrderType(orderType);
        if (orderType == 1) {
            order.setPrice(lawyer.getDocumentPrice() != null ? lawyer.getDocumentPrice() : BigDecimal.ZERO);
        } else {
            order.setPrice(lawyer.getAuditPrice() != null ? lawyer.getAuditPrice() : BigDecimal.ZERO);
        }

        if (order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            Result<Boolean> payResult = userWalletService.escrowPay(
                    userId,
                    order.getPrice(),
                    order.getOrderNo(),
                    "文书服务费",
                    order.getPayPassword()
            );
            if (payResult.getCode() != 200) {
                return Result.error(payResult.getMessage());
            }
        }
        order.setPayStatus(1);
        order.setPayTime(LocalDateTime.now());
        
        documentOrderMapper.insert(order);
        return Result.success("提交成功", true);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        LambdaQueryWrapper<DocumentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DocumentOrder::getLawyerId, getPossibleDocumentLawyerIds(userId));
        
        List<DocumentOrder> allOrders = documentOrderMapper.selectList(wrapper);
        
        long pendingCount = allOrders.stream().filter(o -> o.getStatus() == 1).count();
        long processingCount = allOrders.stream().filter(o -> o.getStatus() == 2).count();
        long completedCount = allOrders.stream().filter(o -> o.getStatus() == 3).count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("pendingCount", pendingCount);
        result.put("processingCount", processingCount);
        result.put("completedCount", completedCount);
        result.put("totalCount", allOrders.size());
        
        return Result.success(result);
    }

    /*@PostMapping("/accept/{id}")
    public Result<String> accept(@PathVariable Long id, HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        
        DocumentOrder order = documentOrderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getLawyerId().equals(lawyerId)) {
            return Result.error("无权操作");
        }
        
        order.setStatus(2);
        documentOrderMapper.updateById(order);
        
        return Result.success("接单成功", null);
    }*/
    /**
     * 律师接单

    @PostMapping("/accept/{id}")
    public Result<String> accept(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 获取当前登录用户的ID（sys_user.id）
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("接单请求 - 用户ID: " + userId);

            if (userId == null) {
                return Result.error("用户未登录");
            }

            // 根据 userId 获取律师信息，得到 lawyer_info.id
            LambdaQueryWrapper<LawyerInfo> lawyerWrapper = new LambdaQueryWrapper<>();
            lawyerWrapper.eq(LawyerInfo::getUserId, userId);
            LawyerInfo lawyer = lawyerInfoMapper.selectOne(lawyerWrapper);

            System.out.println("接单请求 - 律师信息: " + (lawyer != null ? lawyer.getId() + ", " + lawyer.getLawyerName() : "null"));

            if (lawyer == null) {
                return Result.error("您还不是律师，请先申请律师认证");
            }

            DocumentOrder order = documentOrderMapper.selectById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }

            System.out.println("接单请求 - 订单信息: id=" + order.getId() + ", lawyerId=" + order.getLawyerId() + ", status=" + order.getStatus());

            // 关键修复：检查订单是否已经分配给其他律师
            // 如果订单的 lawyer_id 不为空且不等于当前律师，说明已被其他律师接单
            if (order.getLawyerId() != null && order.getLawyerId() > 0) {
                if (!order.getLawyerId().equals(lawyer.getId())) {
                    return Result.error("该订单已被其他律师接单");
                }
            }

            // 检查订单状态是否为待接单
            if (order.getStatus() != 1) {
                String statusText = "";
                switch (order.getStatus()) {
                    case 2: statusText = "进行中"; break;
                    case 3: statusText = "已完成"; break;
                    case 4: statusText = "已取消"; break;
                    default: statusText = "未知状态";
                }
                return Result.error("订单状态已变更，当前状态为: " + statusText);
            }

            // 更新订单
            order.setLawyerId(lawyer.getId());  // 确保设置律师ID
            order.setStatus(2); // 进行中
            order.setStartTime(LocalDateTime.now());

            int result = documentOrderMapper.updateById(order);

            if (result > 0) {
                System.out.println("接单成功 - 订单ID: " + id + ", 律师ID: " + lawyer.getId());
                return Result.success("接单成功", null);
            } else {
                return Result.error("接单失败，请重试");
            }

        } catch (Exception e) {
            System.err.println("接单失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("接单失败: " + e.getMessage());
        }
    } */
    @PostMapping("/accept/{id}")
    public Result<String> accept(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("接单请求 - 用户ID: " + userId);

            if (userId == null) {
                return Result.error("用户未登录");
            }

            // 根据 userId 获取律师信息
            LambdaQueryWrapper<LawyerInfo> lawyerWrapper = new LambdaQueryWrapper<>();
            lawyerWrapper.eq(LawyerInfo::getUserId, userId);
            LawyerInfo lawyer = lawyerInfoMapper.selectOne(lawyerWrapper);

            if (lawyer == null) {
                return Result.error("您还不是律师，请先申请律师认证");
            }

            Long lawyerId = lawyer.getId();

            DocumentOrder order = documentOrderMapper.selectById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 检查订单状态
            if (order.getStatus() != 1) {
                return Result.error("订单状态不是待接单，当前状态: " + order.getStatus());
            }
            if (order.getPayStatus() == null || order.getPayStatus() != 1) {
                return Result.error("订单未支付，无法接单");
            }

            // 更新订单
            order.setLawyerId(lawyerId);
            order.setStatus(2);
            order.setStartTime(LocalDateTime.now());

            int result = documentOrderMapper.updateById(order);

            if (result > 0) {
                System.out.println("接单成功 - 订单ID: " + id + ", 律师ID: " + lawyerId);
                return Result.success("接单成功", null);
            } else {
                return Result.error("接单失败");
            }

        } catch (Exception e) {
            System.err.println("接单失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("接单失败: " + e.getMessage());
        }
    }

    /*@PostMapping("/complete/{id}")
    public Result<String> complete(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        
        DocumentOrder order = documentOrderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getLawyerId().equals(lawyerId)) {
            return Result.error("无权操作");
        }
        
        order.setStatus(3);
        order.setResultFileUrl(params.get("resultFileUrl"));
        order.setResultContent(params.get("resultContent"));
        order.setFinishTime(LocalDateTime.now());
        documentOrderMapper.updateById(order);
        
        return Result.success("完成成功", null);
    }*/
    @PostMapping("/complete/{id}")
    @Transactional
    public Result<String> complete(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            // 获取当前登录用户的ID（sys_user.id）
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("完成订单请求 - 用户ID: " + userId);

            if (userId == null) {
                return Result.error("用户未登录");
            }

            // 根据 userId 获取律师信息，得到 lawyer_info.id
            LambdaQueryWrapper<LawyerInfo> lawyerWrapper = new LambdaQueryWrapper<>();
            lawyerWrapper.eq(LawyerInfo::getUserId, userId);
            LawyerInfo lawyer = lawyerInfoMapper.selectOne(lawyerWrapper);

            if (lawyer == null) {
                return Result.error("您还不是律师，请先申请律师认证");
            }

            Long lawyerId = lawyer.getId();
            System.out.println("律师ID (lawyer_info.id): " + lawyerId);

            DocumentOrder order = documentOrderMapper.selectById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 检查订单是否属于当前律师
            if (order.getLawyerId() == null || !order.getLawyerId().equals(lawyerId)) {
                System.out.println("权限检查失败 - 订单律师ID: " + order.getLawyerId() + ", 当前律师ID: " + lawyerId);
                return Result.error("无权操作此订单");
            }

            // 检查订单状态是否为进行中
            if (order.getStatus() != 2) {
                return Result.error("订单状态不正确，当前状态: " + order.getStatus());
            }

            String resultFileUrl = params.get("resultFileUrl");
            String resultContent = params.get("resultContent");

            if (resultFileUrl == null || resultFileUrl.isEmpty()) {
                return Result.error("请上传结果文件");
            }
            if (order.getPayStatus() == null || order.getPayStatus() != 1) {
                return Result.error("订单未处于已支付托管状态，无法结算");
            }
            if (order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                Result<Boolean> settleResult = userWalletService.settleEscrow(
                        order.getUserId(),
                        lawyer.getUserId(),
                        order.getPrice(),
                        order.getOrderNo(),
                        "文书服务收入"
                );
                if (settleResult.getCode() != 200) {
                    return Result.error(settleResult.getMessage());
                }
            }

            order.setStatus(3); // 已完成
            order.setPayStatus(3);
            order.setResultFileUrl(resultFileUrl);
            order.setResultContent(resultContent);
            order.setFinishTime(LocalDateTime.now());

            int result = documentOrderMapper.updateById(order);

            if (result > 0) {
                if (order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                    IncomeRecord incomeRecord = new IncomeRecord();
                    incomeRecord.setRecordNo("INCOME" + IdUtil.getSnowflakeNextIdStr());
                    incomeRecord.setLawyerId(lawyer.getUserId());
                    incomeRecord.setOrderId(id);
                    incomeRecord.setOrderType(2);
                    incomeRecord.setOrderAmount(order.getPrice());
                    incomeRecord.setCommissionRate(BigDecimal.ZERO);
                    incomeRecord.setCommissionAmount(BigDecimal.ZERO);
                    incomeRecord.setIncomeAmount(order.getPrice());
                    incomeRecord.setStatus(2);
                    incomeRecord.setSettleTime(LocalDateTime.now());
                    incomeRecordMapper.insert(incomeRecord);
                }
                System.out.println("完成订单成功 - 订单ID: " + id);
                return Result.success("完成成功", null);
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.error("更新失败");
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            System.err.println("完成订单失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("完成订单失败: " + e.getMessage());
        }
    }

    /**
     * 用户取消文书订单
     */
    @PostMapping("/cancel/{id}")
    @Transactional
    public Result<String> cancel(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");

            DocumentOrder order = documentOrderMapper.selectById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 验证订单是否属于当前用户
            if (!order.getUserId().equals(userId)) {
                return Result.error("无权操作此订单");
            }

            // 只有待接单状态可以取消
            if (order.getStatus() != 1) {
                return Result.error("订单已被接单或已完成，无法取消");
            }
            if (order.getPayStatus() != null && order.getPayStatus() == 1
                    && order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                Result<Boolean> refundResult = userWalletService.refundEscrow(
                        userId,
                        order.getPrice(),
                        order.getOrderNo(),
                        "文书订单取消退款"
                );
                if (refundResult.getCode() != 200) {
                    return Result.error(refundResult.getMessage());
                }
                order.setPayStatus(2);
            }

            order.setStatus(4); // 已取消
            documentOrderMapper.updateById(order);

            return Result.success("取消成功", null);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return Result.error("取消失败: " + e.getMessage());
        }
    }
}
