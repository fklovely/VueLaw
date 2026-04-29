package com.legal.platform.controller;

import com.legal.platform.common.Result;
import com.legal.platform.entity.SysUser;
import com.legal.platform.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        return sysUserService.login(username, password);
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody SysUser user) {
        return sysUserService.register(user);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return sysUserService.getUserInfo(userId);
    }

    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestBody SysUser user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        return sysUserService.updateUserInfo(user);
    }

    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        return sysUserService.updatePassword(userId, oldPassword, newPassword);
    }
}
