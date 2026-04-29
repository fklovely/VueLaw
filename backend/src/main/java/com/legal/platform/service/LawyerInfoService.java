package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LawyerInfo;

import java.util.Map;

public interface LawyerInfoService extends IService<LawyerInfo> {
    PageResult<LawyerInfo> getLawyerList(Integer page, Integer size, String keyword, String expertise, Integer status);
    Result<LawyerInfo> getLawyerDetail(Long id);
    Result<LawyerInfo> getLawyerInfoByUserId(Long userId);
    Result<Boolean> applyLawyer(LawyerInfo lawyerInfo, Long userId);
    Result<Boolean> auditLawyer(Long id, Integer status, String rejectReason);
    Result<Boolean> updateLawyerInfo(LawyerInfo lawyerInfo);
    Result<Map<String, Object>> getLawyerStatistics(Long lawyerId);
    Result<Map<String, Object>> getAuditStatus(Long userId);
    Result<Boolean> resubmitAudit(Long userId, Map<String, Object> data);
}
