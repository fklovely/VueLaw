package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.service.LawyerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/lawyer")
public class LawyerController {

    @Autowired
    private LawyerInfoService lawyerInfoService;

    @GetMapping("/list")
    public Result<PageResult<LawyerInfo>> getLawyerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false) Integer status) {
        return Result.success(lawyerInfoService.getLawyerList(page, size, keyword, expertise, status));
    }

    @GetMapping("/detail/{id}")
    public Result<LawyerInfo> getLawyerDetail(@PathVariable Long id) {
        return lawyerInfoService.getLawyerDetail(id);
    }

    @GetMapping("/info")
    public Result<LawyerInfo> getLawyerInfoByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return lawyerInfoService.getLawyerInfoByUserId(userId);
    }

    @PostMapping("/apply")
    public Result<Boolean> applyLawyer(@RequestBody LawyerInfo lawyerInfo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return lawyerInfoService.applyLawyer(lawyerInfo, userId);
    }

    @PostMapping("/audit")
    public Result<Boolean> auditLawyer(@RequestBody Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        Integer status = Integer.parseInt(params.get("status").toString());
        String rejectReason = (String) params.get("rejectReason");
        return lawyerInfoService.auditLawyer(id, status, rejectReason);
    }

    @PutMapping
    public Result<Boolean> updateLawyerInfo(@RequestBody LawyerInfo lawyerInfo) {
        return lawyerInfoService.updateLawyerInfo(lawyerInfo);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getLawyerStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return lawyerInfoService.getLawyerStatistics(userId);
    }

    @GetMapping("/audit-status")
    public Result<Map<String, Object>> getAuditStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return lawyerInfoService.getAuditStatus(userId);
    }

    @PostMapping("/resubmit")
    public Result<Boolean> resubmitAudit(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return lawyerInfoService.resubmitAudit(userId, data);
    }
}
