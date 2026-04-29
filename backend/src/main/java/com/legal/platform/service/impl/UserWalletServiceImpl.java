package com.legal.platform.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.Result;
import com.legal.platform.entity.UserWallet;
import com.legal.platform.entity.WalletRecord;
import com.legal.platform.mapper.UserWalletMapper;
import com.legal.platform.mapper.WalletRecordMapper;
import com.legal.platform.service.UserWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService {

    private static final Logger logger = LoggerFactory.getLogger(UserWalletServiceImpl.class);

    @Autowired
    private WalletRecordMapper walletRecordMapper;

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private UserWallet getWalletByUserId(Long userId) {
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        return this.getOne(wrapper);
    }

    private UserWallet createWallet(Long userId) {
        UserWallet wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFrozenAmount(BigDecimal.ZERO);
        wallet.setPayPasswordSet(0);
        wallet.setStatus(1);
        this.save(wallet);
        return wallet;
    }

    private void insertWalletRecord(Long userId, String orderNo, Integer type, BigDecimal amount,
                                    BigDecimal beforeBalance, BigDecimal afterBalance, String description) {
        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setOrderNo(orderNo);
        record.setType(type);
        record.setAmount(amount);
        record.setBeforeBalance(beforeBalance);
        record.setAfterBalance(afterBalance);
        record.setDescription(description);
        walletRecordMapper.insert(record);
    }

    @Override
    public Result<UserWallet> getOrCreateWallet(Long userId) {
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = this.getOne(wrapper);
        
        if (wallet == null) {
            wallet = new UserWallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozenAmount(BigDecimal.ZERO);
            wallet.setPayPasswordSet(0);
            wallet.setStatus(1);
            this.save(wallet);
            
            logger.info("========== 模拟短信发送 ==========");
            logger.info("╔══════════════════════════════════════════════════════════════╗");
            logger.info("║                      模拟短信发送                              ║");
            logger.info("╠══════════════════════════════════════════════════════════════╣");
            logger.info("║ 短信类型: 钱包开通通知");
            logger.info("║ 短信内容: 【未成年人保护法律平台】您的钱包已开通，可以开始充值使用啦！");
            logger.info("║ 发送时间: {}", LocalDateTime.now());
            logger.info("╚══════════════════════════════════════════════════════════════╝");
            logger.info("");
        }
        
        return Result.success(wallet);
    }

    @Override
    public Result<BigDecimal> getBalance(Long userId) {
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = this.getOne(wrapper);
        
        if (wallet == null) {
            return Result.success(BigDecimal.ZERO);
        }
        
        return Result.success(wallet.getBalance());
    }

    @Override
    @Transactional
    public Result<Boolean> recharge(Long userId, BigDecimal amount, String payPassword) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("充值金额必须大于0");
        }
        
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = this.getOne(wrapper);
        
        if (wallet == null) {
            return Result.error("钱包不存在");
        }
        
        if (wallet.getStatus() != 1) {
            return Result.error("钱包已被冻结");
        }
        
        if (payPassword != null && !payPassword.isEmpty() && wallet.getPayPasswordSet() == 1) {
            if (!BCrypt.checkpw(payPassword, wallet.getPayPassword())) {
                return Result.error("支付密码错误");
            }
        }
        
        BigDecimal beforeBalance = wallet.getBalance();
        BigDecimal afterBalance = beforeBalance.add(amount);
        wallet.setBalance(afterBalance);
        this.updateById(wallet);
        
        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setOrderNo("RCH" + System.currentTimeMillis());
        record.setType(1);
        record.setAmount(amount);
        record.setBeforeBalance(beforeBalance);
        record.setAfterBalance(afterBalance);
        record.setDescription("余额充值");
        walletRecordMapper.insert(record);
        
        logger.info("========== 模拟短信发送 ==========");
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║                      模拟短信发送                              ║");
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        logger.info("║ 短信类型: 充值到账通知");
        logger.info("║ 短信内容: 【未成年人保护法律平台】您已成功充值 ¥{}，当前余额 ¥{}", amount, afterBalance);
        logger.info("║ 发送时间: {}", LocalDateTime.now());
        logger.info("╚══════════════════════════════════════════════════════════════╝");
        logger.info("");
        
        return Result.success("充值成功", true);
    }

    @Override
    @Transactional
    public Result<Boolean> pay(Long userId, BigDecimal amount, String orderNo, String description, String payPassword) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("支付金额必须大于0");
        }
        
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = this.getOne(wrapper);
        
        if (wallet == null) {
            return Result.error("钱包不存在，请先开通");
        }
        
        if (wallet.getStatus() != 1) {
            return Result.error("钱包已被冻结");
        }
        
        if (wallet.getPayPasswordSet() != 1 || wallet.getPayPassword() == null || wallet.getPayPassword().isEmpty()) {
            return Result.error("请先设置支付密码");
        }
        
        if (!BCrypt.checkpw(payPassword, wallet.getPayPassword())) {
            return Result.error("支付密码错误");
        }
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            return Result.error("余额不足");
        }
        
        BigDecimal beforeBalance = wallet.getBalance();
        BigDecimal afterBalance = beforeBalance.subtract(amount);
        wallet.setBalance(afterBalance);
        this.updateById(wallet);
        
        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setOrderNo(orderNo);
        record.setType(2);
        record.setAmount(amount);
        record.setBeforeBalance(beforeBalance);
        record.setAfterBalance(afterBalance);
        record.setDescription(description);
        walletRecordMapper.insert(record);
        
        return Result.success("支付成功", true);
    }

    @Override
    @Transactional
    public Result<Boolean> escrowPay(Long userId, BigDecimal amount, String orderNo, String description, String payPassword) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("支付金额必须大于0");
        }

        UserWallet wallet = getWalletByUserId(userId);
        if (wallet == null) {
            return Result.error("钱包不存在，请先开通");
        }
        if (wallet.getStatus() == null || wallet.getStatus() != 1) {
            return Result.error("钱包已被冻结");
        }
        if (wallet.getPayPasswordSet() == null || wallet.getPayPasswordSet() != 1
                || wallet.getPayPassword() == null || wallet.getPayPassword().isEmpty()) {
            return Result.error("请先设置支付密码");
        }
        if (payPassword == null || payPassword.isEmpty() || !BCrypt.checkpw(payPassword, wallet.getPayPassword())) {
            return Result.error("支付密码错误");
        }

        BigDecimal beforeBalance = zeroIfNull(wallet.getBalance());
        if (beforeBalance.compareTo(amount) < 0) {
            return Result.error("余额不足");
        }

        BigDecimal afterBalance = beforeBalance.subtract(amount);
        wallet.setBalance(afterBalance);
        wallet.setFrozenAmount(zeroIfNull(wallet.getFrozenAmount()).add(amount));
        this.updateById(wallet);

        insertWalletRecord(userId, orderNo, 2, amount, beforeBalance, afterBalance, description);
        return Result.success("支付成功，资金已进入平台托管", true);
    }

    @Override
    @Transactional
    public Result<Boolean> refundEscrow(Long userId, BigDecimal amount, String orderNo, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("退款金额必须大于0");
        }

        UserWallet wallet = getWalletByUserId(userId);
        if (wallet == null) {
            return Result.error("钱包不存在");
        }

        BigDecimal frozenAmount = zeroIfNull(wallet.getFrozenAmount());
        if (frozenAmount.compareTo(amount) < 0) {
            return Result.error("托管金额不足，无法退款");
        }

        BigDecimal beforeBalance = zeroIfNull(wallet.getBalance());
        BigDecimal afterBalance = beforeBalance.add(amount);
        wallet.setBalance(afterBalance);
        wallet.setFrozenAmount(frozenAmount.subtract(amount));
        this.updateById(wallet);

        insertWalletRecord(userId, orderNo, 3, amount, beforeBalance, afterBalance, description);
        return Result.success("退款成功", true);
    }

    @Override
    @Transactional
    public Result<Boolean> settleEscrow(Long payerUserId, Long payeeUserId, BigDecimal amount, String orderNo, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("结算金额必须大于0");
        }

        UserWallet payerWallet = getWalletByUserId(payerUserId);
        if (payerWallet == null) {
            return Result.error("付款方钱包不存在");
        }

        BigDecimal frozenAmount = zeroIfNull(payerWallet.getFrozenAmount());
        if (frozenAmount.compareTo(amount) < 0) {
            return Result.error("托管金额不足，无法结算");
        }

        UserWallet payeeWallet = getWalletByUserId(payeeUserId);
        if (payeeWallet == null) {
            payeeWallet = createWallet(payeeUserId);
        }
        if (payeeWallet.getStatus() == null || payeeWallet.getStatus() != 1) {
            return Result.error("收款方钱包已被冻结");
        }

        payerWallet.setFrozenAmount(frozenAmount.subtract(amount));
        this.updateById(payerWallet);

        BigDecimal beforeBalance = zeroIfNull(payeeWallet.getBalance());
        BigDecimal afterBalance = beforeBalance.add(amount);
        payeeWallet.setBalance(afterBalance);
        this.updateById(payeeWallet);

        insertWalletRecord(payeeUserId, orderNo, 4, amount, beforeBalance, afterBalance, description);
        return Result.success("结算成功", true);
    }

    @Override
    public Result<Boolean> setPayPassword(Long userId, String oldPassword, String newPassword) {
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = this.getOne(wrapper);
        
        if (wallet == null) {
            wallet = new UserWallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozenAmount(BigDecimal.ZERO);
            wallet.setStatus(1);
            this.save(wallet);
        }
        
        if (wallet.getPayPasswordSet() == 1) {
            if (oldPassword == null || oldPassword.isEmpty()) {
                return Result.error("请输入原支付密码");
            }
            if (!BCrypt.checkpw(oldPassword, wallet.getPayPassword())) {
                return Result.error("原支付密码错误");
            }
        }
        
        wallet.setPayPassword(BCrypt.hashpw(newPassword));
        wallet.setPayPasswordSet(1);
        this.updateById(wallet);
        
        return Result.success("支付密码设置成功", true);
    }

    @Override
    public Result<List<WalletRecord>> getRecords(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<WalletRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletRecord::getUserId, userId)
                .orderByDesc(WalletRecord::getCreateTime)
                .last("LIMIT " + size + " OFFSET " + ((page - 1) * size));
        
        List<WalletRecord> records = walletRecordMapper.selectList(wrapper);
        return Result.success(records);
    }
}
