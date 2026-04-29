package com.legal.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.IncomeRecord;
import com.legal.platform.mapper.IncomeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/income")
public class IncomeController {

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @GetMapping("/list")
    public Result<PageResult<IncomeRecord>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        
        LambdaQueryWrapper<IncomeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IncomeRecord::getLawyerId, lawyerId);
        if (status != null) {
            wrapper.eq(IncomeRecord::getStatus, status);
        }
        wrapper.orderByDesc(IncomeRecord::getCreateTime);
        
        IPage<IncomeRecord> pageResult = incomeRecordMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(pageResult.getRecords(), pageResult.getTotal()));
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        
        LambdaQueryWrapper<IncomeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IncomeRecord::getLawyerId, lawyerId);
        
        List<IncomeRecord> allRecords = incomeRecordMapper.selectList(wrapper);
        
        BigDecimal totalIncome = allRecords.stream()
                .filter(r -> r.getStatus() == 2)
                .map(IncomeRecord::getIncomeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pendingIncome = allRecords.stream()
                .filter(r -> r.getStatus() == 1)
                .map(IncomeRecord::getIncomeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal settledIncome = allRecords.stream()
                .filter(r -> r.getStatus() == 2)
                .map(IncomeRecord::getIncomeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal monthIncome = BigDecimal.ZERO;
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("pendingIncome", pendingIncome);
        result.put("settledIncome", settledIncome);
        result.put("monthIncome", monthIncome);
        
        return Result.success(result);
    }
}
