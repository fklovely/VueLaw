package com.legal.platform.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.entity.SysUser;
import com.legal.platform.mapper.LawyerInfoMapper;
import com.legal.platform.mapper.SysUserMapper;
import com.legal.platform.service.SysUserService;
import com.legal.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LawyerInfoMapper lawyerInfoMapper;

    @Override
    public Result<Map<String, Object>> login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = this.getOne(wrapper);
        if (user == null) {
            return Result.error("用户名不存在");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.error("密码错误");
        }
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getRealName());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());
        return Result.success("登录成功", data);
    }

    @Override
    public Result<Map<String, Object>> register(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }
        if (StringUtils.hasText(user.getPhone())) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getPhone, user.getPhone());
            if (this.count(wrapper) > 0) {
                return Result.error("手机号已被注册");
            }
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        String role = user.getRole();
        if ("lawyer".equals(role)) {
            user.setRole("client");
            user.setAuditStatus(1);
            user.setStatus(1);
            this.save(user);
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            return Result.success("注册成功，您的律师身份申请已提交，请等待管理员审核", data);
        } else {
            user.setRole("client");
            user.setAuditStatus(0);
            user.setStatus(1);
            this.save(user);
            String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getRealName());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("role", user.getRole());
            return Result.success("注册成功", data);
        }
    }

    @Override
    public Result<Map<String, Object>> getUserInfo(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        return Result.success(data);
    }

    @Override
    public Result<Boolean> updateUserInfo(SysUser user) {
        SysUser existUser = this.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        existUser.setRealName(user.getRealName());
        existUser.setPhone(user.getPhone());
        existUser.setEmail(user.getEmail());
        existUser.setAvatar(user.getAvatar());
        existUser.setGender(user.getGender());
        existUser.setAge(user.getAge());
        this.updateById(existUser);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        this.updateById(user);
        return Result.success("密码修改成功", true);
    }

    @Override
    public PageResult<SysUser> getUserList(Integer page, Integer size, String role, String keyword, Integer status, Integer auditStatus) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        if (auditStatus != null) {
            wrapper.eq(SysUser::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> pageResult = this.page(new Page<>(page, size), wrapper);
        pageResult.getRecords().forEach(u -> u.setPassword(null));
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<Boolean> addUser(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setAuditStatus(0);
        user.setStatus(1);
        this.save(user);
        return Result.success("添加成功", true);
    }

    @Override
    public void saveLawyerInfo(LawyerInfo lawyerInfo) {
        lawyerInfoMapper.insert(lawyerInfo);
    }
}
