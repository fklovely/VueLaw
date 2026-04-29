package com.legal.platform.service;

import com.legal.platform.common.Result;

import java.util.Map;

public interface SmsService {
    Result<Map<String, Object>> sendSms(String phone, String type);
    Result<Boolean> verifyCode(String phone, String code);
    void sendAuditRejectSms(String phone, String reason);
}
