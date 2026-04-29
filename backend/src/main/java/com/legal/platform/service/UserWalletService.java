package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.Result;
import com.legal.platform.entity.UserWallet;
import com.legal.platform.entity.WalletRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserWalletService extends IService<UserWallet> {
    Result<UserWallet> getOrCreateWallet(Long userId);
    Result<BigDecimal> getBalance(Long userId);
    Result<Boolean> recharge(Long userId, BigDecimal amount, String payPassword);
    Result<Boolean> pay(Long userId, BigDecimal amount, String orderNo, String description, String payPassword);
    Result<Boolean> escrowPay(Long userId, BigDecimal amount, String orderNo, String description, String payPassword);
    Result<Boolean> refundEscrow(Long userId, BigDecimal amount, String orderNo, String description);
    Result<Boolean> settleEscrow(Long payerUserId, Long payeeUserId, BigDecimal amount, String orderNo, String description);
    Result<Boolean> setPayPassword(Long userId, String oldPassword, String newPassword);
    Result<List<WalletRecord>> getRecords(Long userId, Integer page, Integer size);
}
