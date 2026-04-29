package com.legal.platform.controller;

import com.legal.platform.common.Result;
import com.legal.platform.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public Result<Map<String, Object>> sendSms(@RequestParam String phone, @RequestParam String type) {
        return smsService.sendSms(phone, type);
    }

    @PostMapping("/verify")
    public Result<Boolean> verifyCode(@RequestParam String phone, @RequestParam String code) {
        return smsService.verifyCode(phone, code);
    }
}
