package com.legal.platform.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.entity.SysUser;
import com.legal.platform.service.SmsService;
import com.legal.platform.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SmsService smsService;

    @GetMapping("/list")
    public Result<PageResult<SysUser>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer auditStatus) {
        return Result.success(sysUserService.getUserList(page, size, role, keyword, status, auditStatus));
    }

    @PostMapping
    public Result<Boolean> addUser(@RequestBody SysUser user) {
        return sysUserService.addUser(user);
    }

    @PutMapping("/status/{id}")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.success("操作成功", true);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success("删除成功", true);
    }

    @PutMapping("/audit/{id}")
    public Result<Boolean> auditUser(@PathVariable Long id, @RequestParam Integer auditStatus, @RequestParam(required = false) String auditRemark) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getAuditStatus() == null || user.getAuditStatus() != 1) {
            return Result.error("该用户没有待审核的申请");
        }
        user.setAuditStatus(auditStatus);
        user.setAuditRemark(auditRemark);
        if (auditStatus == 2) {
            user.setRole("lawyer");
            LawyerInfo lawyerInfo = new LawyerInfo();
            lawyerInfo.setUserId(user.getId());
            lawyerInfo.setLawyerName(user.getRealName());
            lawyerInfo.setLawyerNo("LAW" + System.currentTimeMillis());
            lawyerInfo.setPhone(user.getPhone());
            lawyerInfo.setEmail(user.getEmail());
            lawyerInfo.setAvatar(user.getAvatar());
            lawyerInfo.setStatus(1);
            lawyerInfo.setRating(new java.math.BigDecimal("5.0"));
            lawyerInfo.setServiceCount(0);
            lawyerInfo.setCaseCount(0);
            sysUserService.saveLawyerInfo(lawyerInfo);
        } else if (auditStatus == 3) {
            user.setAuditRemark(auditRemark);
        }
        sysUserService.updateById(user);
        
        String msg;
        if (auditStatus == 2) {
            msg = "审核通过，该用户已成为律师";
        } else {
            msg = "已拒绝该用户的律师申请";
            String phone = user.getPhone() != null ? user.getPhone() : "未填写";
            smsService.sendAuditRejectSms(phone, auditRemark);
            msg += "，已发送短信通知用户";
        }
        return Result.success(msg, true);
    }
}
