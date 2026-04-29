package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.entity.SysUser;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    Result<Map<String, Object>> login(String username, String password);
    Result<Map<String, Object>> register(SysUser user);
    Result<Map<String, Object>> getUserInfo(Long userId);
    Result<Boolean> updateUserInfo(SysUser user);
    Result<Boolean> updatePassword(Long userId, String oldPassword, String newPassword);
    PageResult<SysUser> getUserList(Integer page, Integer size, String role, String keyword, Integer status, Integer auditStatus);
    Result<Boolean> addUser(SysUser user);
    void saveLawyerInfo(LawyerInfo lawyerInfo);
}
