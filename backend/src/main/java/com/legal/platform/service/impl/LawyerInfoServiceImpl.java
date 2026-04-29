package com.legal.platform.service.impl;

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
import com.legal.platform.entity.SysUser;
import com.legal.platform.mapper.ConsultOrderMapper;
import com.legal.platform.mapper.IncomeRecordMapper;
import com.legal.platform.mapper.LawyerInfoMapper;
import com.legal.platform.mapper.ServiceEvaluationMapper;
import com.legal.platform.mapper.SysUserMapper;
import com.legal.platform.service.LawyerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LawyerInfoServiceImpl extends ServiceImpl<LawyerInfoMapper, LawyerInfo> implements LawyerInfoService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ConsultOrderMapper consultOrderMapper;

    @Autowired
    private ServiceEvaluationMapper serviceEvaluationMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Override
    public PageResult<LawyerInfo> getLawyerList(Integer page, Integer size, String keyword, String expertise, Integer status) {
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(LawyerInfo::getLawyerName, keyword)
                    .or().like(LawyerInfo::getLawFirm, keyword));
        }
        if (StringUtils.hasText(expertise)) {
            wrapper.like(LawyerInfo::getExpertises, expertise);
        }
        if (status != null) {
            wrapper.eq(LawyerInfo::getStatus, status);
        }
        wrapper.orderByDesc(LawyerInfo::getRating);
        IPage<LawyerInfo> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<LawyerInfo> getLawyerDetail(Long id) {
        LawyerInfo lawyerInfo = this.getById(id);
        if (lawyerInfo == null) {
            return Result.error("律师不存在");
        }
        return Result.success(lawyerInfo);
    }

    @Override
    public Result<LawyerInfo> getLawyerInfoByUserId(Long userId) {
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerInfo::getUserId, userId);
        LawyerInfo lawyerInfo = this.getOne(wrapper);
        if (lawyerInfo == null) {
            return Result.error("律师信息不存在");
        }
        return Result.success(lawyerInfo);
    }

    @Override
    public Result<Boolean> applyLawyer(LawyerInfo lawyerInfo, Long userId) {
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerInfo::getUserId, userId);
        if (this.count(wrapper) > 0) {
            return Result.error("您已申请过律师认证");
        }
        lawyerInfo.setUserId(userId);
        lawyerInfo.setStatus(0);
        this.save(lawyerInfo);
        SysUser user = sysUserMapper.selectById(userId);
        user.setRole("lawyer");
        sysUserMapper.updateById(user);
        return Result.success("申请成功，请等待审核", true);
    }

    @Override
    public Result<Boolean> auditLawyer(Long id, Integer status, String rejectReason) {
        LawyerInfo lawyerInfo = this.getById(id);
        if (lawyerInfo == null) {
            return Result.error("律师不存在");
        }
        lawyerInfo.setStatus(status);
        if (status == 2 && StringUtils.hasText(rejectReason)) {
            lawyerInfo.setRejectReason(rejectReason);
        }
        this.updateById(lawyerInfo);
        return Result.success("审核完成", true);
    }

    @Override
    public Result<Boolean> updateLawyerInfo(LawyerInfo lawyerInfo) {
        LawyerInfo existLawyer = this.getById(lawyerInfo.getId());
        if (existLawyer == null) {
            return Result.error("律师不存在");
        }
        existLawyer.setLawyerName(lawyerInfo.getLawyerName());
        existLawyer.setPhone(lawyerInfo.getPhone());
        existLawyer.setEmail(lawyerInfo.getEmail());
        existLawyer.setProvince(lawyerInfo.getProvince());
        existLawyer.setCity(lawyerInfo.getCity());
        existLawyer.setLawFirm(lawyerInfo.getLawFirm());
        existLawyer.setEducation(lawyerInfo.getEducation());
        existLawyer.setProfessionalTitle(lawyerInfo.getProfessionalTitle());
        existLawyer.setProfile(lawyerInfo.getProfile());
        existLawyer.setExpertises(lawyerInfo.getExpertises());
        existLawyer.setConsultPrice(lawyerInfo.getConsultPrice());
        existLawyer.setDeepConsultPrice(lawyerInfo.getDeepConsultPrice());
        existLawyer.setDocumentPrice(lawyerInfo.getDocumentPrice());
        existLawyer.setAuditPrice(lawyerInfo.getAuditPrice());
        existLawyer.setVideoPrice(lawyerInfo.getVideoPrice());
        this.updateById(existLawyer);
        SysUser user = sysUserMapper.selectById(existLawyer.getUserId());
        if (user != null) {
            user.setRealName(lawyerInfo.getLawyerName());
            user.setPhone(lawyerInfo.getPhone());
            user.setEmail(lawyerInfo.getEmail());
            user.setAvatar(lawyerInfo.getAvatar());
            sysUserMapper.updateById(user);
        }
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Map<String, Object>> getLawyerStatistics(Long lawyerId) {
        Map<String, Object> stats = new HashMap<>();
        
        LambdaQueryWrapper<ConsultOrder> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(ConsultOrder::getLawyerId, lawyerId)
                .in(ConsultOrder::getStatus, 1, 2, 3);
        long pendingOrders = consultOrderMapper.selectCount(pendingWrapper);
        stats.put("pendingOrders", pendingOrders);
        
        LambdaQueryWrapper<ConsultOrder> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(ConsultOrder::getLawyerId, lawyerId).eq(ConsultOrder::getStatus, 4);
        long completedOrders = consultOrderMapper.selectCount(completedWrapper);
        stats.put("completedOrders", completedOrders);
        
        LambdaQueryWrapper<ServiceEvaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(ServiceEvaluation::getLawyerId, lawyerId);
        List<ServiceEvaluation> evaluations = serviceEvaluationMapper.selectList(evalWrapper);
        double avgScore = evaluations.stream().mapToInt(ServiceEvaluation::getTotalScore).average().orElse(5.0);
        stats.put("avgScore", Math.round(avgScore * 100.0) / 100.0);
        stats.put("evalCount", evaluations.size());
        
        LambdaQueryWrapper<IncomeRecord> allIncomeWrapper = new LambdaQueryWrapper<>();
        allIncomeWrapper.eq(IncomeRecord::getLawyerId, lawyerId);
        List<IncomeRecord> allIncomeRecords = incomeRecordMapper.selectList(allIncomeWrapper);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal pendingIncome = BigDecimal.ZERO;
        BigDecimal settledIncome = BigDecimal.ZERO;
        BigDecimal monthIncome = BigDecimal.ZERO;
        
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDateTime monthStart = now.withDayOfMonth(1).atStartOfDay();
        
        for (IncomeRecord record : allIncomeRecords) {
            if (record.getIncomeAmount() != null) {
                totalIncome = totalIncome.add(record.getIncomeAmount());
                
                int status = record.getStatus() != null ? record.getStatus() : 1;
                if (status == 1) {
                    pendingIncome = pendingIncome.add(record.getIncomeAmount());
                } else {
                    settledIncome = settledIncome.add(record.getIncomeAmount());
                }
                
                if (record.getCreateTime() != null && !record.getCreateTime().isBefore(monthStart)) {
                    monthIncome = monthIncome.add(record.getIncomeAmount());
                }
            }
        }
        
        stats.put("totalIncome", totalIncome);
        stats.put("pendingIncome", pendingIncome);
        stats.put("settledIncome", settledIncome);
        stats.put("monthIncome", monthIncome);
        
        return Result.success(stats);
    }

    @Override
    public Result<Map<String, Object>> getAuditStatus(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("auditStatus", user.getAuditStatus());
        result.put("auditRemark", user.getAuditRemark());
        result.put("role", user.getRole());
        result.put("realName", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        
        if (user.getAuditStatus() != null && user.getAuditStatus() == 2) {
            LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LawyerInfo::getUserId, userId);
            LawyerInfo lawyerInfo = this.getOne(wrapper);
            if (lawyerInfo != null) {
                result.put("lawyerInfo", lawyerInfo);
            }
        }
        
        return Result.success(result);
    }

    @Override
    public Result<Boolean> resubmitAudit(Long userId, Map<String, Object> data) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (user.getAuditStatus() == null || user.getAuditStatus() != 3) {
            return Result.error("当前状态不允许重新提交审核");
        }
        
        if (data.containsKey("realName")) {
            user.setRealName((String) data.get("realName"));
        }
        if (data.containsKey("phone")) {
            user.setPhone((String) data.get("phone"));
        }
        if (data.containsKey("email")) {
            user.setEmail((String) data.get("email"));
        }
        user.setAuditStatus(1);
        user.setAuditRemark(null);
        sysUserMapper.updateById(user);
        
        return Result.success("重新提交成功，请等待审核", true);
    }
}
