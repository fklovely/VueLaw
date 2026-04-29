package com.legal.platform.controller;

import com.legal.platform.common.Result;
import com.legal.platform.service.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private UserWalletService userWalletService;

    @GetMapping
    public Result<?> getWallet(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userWalletService.getOrCreateWallet(userId);
    }

    @GetMapping("/balance")
    public Result<BigDecimal> getBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userWalletService.getBalance(userId);
    }

    @PostMapping("/recharge")
    public Result<Boolean> recharge(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BigDecimal amount = new BigDecimal(data.get("amount").toString());
        String payPassword = data.get("payPassword") != null ? data.get("payPassword").toString() : null;
        return userWalletService.recharge(userId, amount, payPassword);
    }

    @PostMapping("/pay")
    public Result<Boolean> pay(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BigDecimal amount = new BigDecimal(data.get("amount").toString());
        String orderNo = data.get("orderNo").toString();
        String description = data.get("description").toString();
        String payPassword = data.get("payPassword").toString();
        return userWalletService.pay(userId, amount, orderNo, description, payPassword);
    }

    @PutMapping("/pay-password")
    public Result<Boolean> setPayPassword(@RequestBody Map<String, String> data, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        return userWalletService.setPayPassword(userId, oldPassword, newPassword);
    }

    @GetMapping("/records")
    public Result<?> getRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userWalletService.getRecords(userId, page, size);
    }
}
