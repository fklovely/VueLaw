package com.legal.platform.service.impl;

import com.legal.platform.common.Result;
import com.legal.platform.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    
    private final Map<String, String> smsCodeCache = new ConcurrentHashMap<>();
    private final Map<String, Long> smsExpireCache = new ConcurrentHashMap<>();
    
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;

    @Override
    public Result<Map<String, Object>> sendSms(String phone, String type) {
        if (phone == null || phone.length() != 11) {
            return Result.error("手机号格式不正确");
        }
        
        String code = generateCode();
        String cacheKey = phone + "_" + type;
        smsCodeCache.put(cacheKey, code);
        smsExpireCache.put(cacheKey, System.currentTimeMillis() + CODE_EXPIRE_TIME);
        
        String typeText = getTypeText(type);
        String content = "【未成年人保护法律平台】您的" + typeText + "验证码是：" + code + "，5分钟内有效，请勿泄露给他人。";
        
        logger.info("========== 模拟短信发送 ==========");
        logger.info("接收号码: {}", phone);
        logger.info("短信类型: {}", typeText);
        logger.info("验证码: {}", code);
        logger.info("短信内容: {}", content);
        logger.info("发送时间: {}", new java.util.Date());
        logger.info("==================================");
        
        Map<String, Object> result = new HashMap<>();
        result.put("phone", phone);
        result.put("type", type);
        result.put("message", "验证码已发送，请注意查收");
        result.put("code", code);
        
        return Result.success("短信发送成功", result);
    }

    @Override
    public Result<Boolean> verifyCode(String phone, String code) {
        if (phone == null || code == null) {
            return Result.error("参数错误");
        }
        
        String cacheKey = phone + "_verify";
        String cachedCode = smsCodeCache.get(cacheKey);
        Long expireTime = smsExpireCache.get(cacheKey);
        
        if (cachedCode == null || expireTime == null) {
            return Result.error("验证码已过期，请重新获取");
        }
        
        if (System.currentTimeMillis() > expireTime) {
            smsCodeCache.remove(cacheKey);
            smsExpireCache.remove(cacheKey);
            return Result.error("验证码已过期，请重新获取");
        }
        
        if (!cachedCode.equals(code)) {
            return Result.error("验证码错误");
        }
        
        smsCodeCache.remove(cacheKey);
        smsExpireCache.remove(cacheKey);
        
        return Result.success("验证成功", true);
    }
    
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    private String getTypeText(String type) {
        switch (type) {
            case "register":
                return "注册";
            case "login":
                return "登录";
            case "reset":
                return "重置密码";
            case "verify":
                return "身份验证";
            default:
                return "验证";
        }
    }

    @Override
    public void sendAuditRejectSms(String phone, String reason) {
        String content = "【未成年人保护法律平台】您的律师认证申请未通过审核。原因：" + (reason != null ? reason : "无") + "。请修改后重新提交申请。";
        
        logger.info("");
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║                      模拟短信发送                              ║");
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        logger.info("║ 接收号码: {}", phone);
        logger.info("║ 短信类型: 律师认证审核通知");
        logger.info("║ 短信内容: {}", content);
        logger.info("║ 发送时间: {}", new java.util.Date());
        logger.info("╚══════════════════════════════════════════════════════════════╝");
        logger.info("");
    }
}
