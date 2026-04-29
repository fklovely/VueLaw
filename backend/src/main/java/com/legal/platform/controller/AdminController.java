package com.legal.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.legal.platform.common.Result;
import com.legal.platform.entity.*;
import com.legal.platform.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private LawyerInfoMapper lawyerInfoMapper;

    @Autowired
    private ConsultOrderMapper consultOrderMapper;

    @Autowired
    private DocumentOrderMapper documentOrderMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private LegalCaseMapper legalCaseMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private ServiceEvaluationMapper serviceEvaluationMapper;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        
        long userCount = sysUserMapper.selectCount(new LambdaQueryWrapper<>());
        stats.put("userCount", userCount);
        
        LambdaQueryWrapper<SysUser> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.ge(SysUser::getCreateTime, todayStart);
        long todayUserCount = sysUserMapper.selectCount(todayUserWrapper);
        stats.put("todayUserCount", todayUserCount);
        
        LambdaQueryWrapper<LawyerInfo> lawyerWrapper = new LambdaQueryWrapper<>();
        lawyerWrapper.eq(LawyerInfo::getStatus, 1);
        long lawyerCount = lawyerInfoMapper.selectCount(lawyerWrapper);
        stats.put("lawyerCount", lawyerCount);
        
        long consultCount = consultOrderMapper.selectCount(new LambdaQueryWrapper<>());
        stats.put("consultCount", consultCount);
        
        LambdaQueryWrapper<ConsultOrder> todayConsultWrapper = new LambdaQueryWrapper<>();
        todayConsultWrapper.ge(ConsultOrder::getCreateTime, todayStart);
        long todayConsultCount = consultOrderMapper.selectCount(todayConsultWrapper);
        stats.put("todayConsultCount", todayConsultCount);
        
        long docOrderCount = documentOrderMapper.selectCount(new LambdaQueryWrapper<>());
        stats.put("docOrderCount", docOrderCount);
        
        LambdaQueryWrapper<DocumentOrder> todayDocWrapper = new LambdaQueryWrapper<>();
        todayDocWrapper.ge(DocumentOrder::getCreateTime, todayStart);
        long todayDocCount = documentOrderMapper.selectCount(todayDocWrapper);
        stats.put("todayDocCount", todayDocCount);
        
        LambdaQueryWrapper<IncomeRecord> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.isNotNull(IncomeRecord::getIncomeAmount);
        BigDecimal totalAmount = incomeRecordMapper.selectList(incomeWrapper).stream()
                .map(IncomeRecord::getIncomeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalAmount", "¥" + totalAmount.toString());
        
        LambdaQueryWrapper<IncomeRecord> todayIncomeWrapper = new LambdaQueryWrapper<>();
        todayIncomeWrapper.isNotNull(IncomeRecord::getIncomeAmount)
                .ge(IncomeRecord::getCreateTime, todayStart);
        BigDecimal todayAmount = incomeRecordMapper.selectList(todayIncomeWrapper).stream()
                .map(IncomeRecord::getIncomeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("todayAmount", "¥" + todayAmount.toString());
        
        long caseCount = legalCaseMapper.selectCount(new LambdaQueryWrapper<>());
        stats.put("caseCount", caseCount);
        
        long articleCount = articleMapper.selectCount(new LambdaQueryWrapper<>());
        stats.put("articleCount", articleCount);
        
        long noticeCount = noticeMapper.selectCount(new LambdaQueryWrapper<>());
        stats.put("noticeCount", noticeCount);
        
        long evaluationCount = serviceEvaluationMapper.selectCount(new LambdaQueryWrapper<>());
        stats.put("evaluationCount", evaluationCount);
        
        LambdaQueryWrapper<SysUser> pendingLawyerWrapper = new LambdaQueryWrapper<>();
        pendingLawyerWrapper.eq(SysUser::getAuditStatus, 1);
        long pendingLawyer = sysUserMapper.selectCount(pendingLawyerWrapper);
        stats.put("pendingLawyer", pendingLawyer);
        
        LambdaQueryWrapper<ConsultOrder> pendingConsultWrapper = new LambdaQueryWrapper<>();
        pendingConsultWrapper.in(ConsultOrder::getStatus, 1, 2, 3);
        long pendingConsult = consultOrderMapper.selectCount(pendingConsultWrapper);
        stats.put("pendingConsult", pendingConsult);
        
        LambdaQueryWrapper<Article> pendingArticleWrapper = new LambdaQueryWrapper<>();
        pendingArticleWrapper.eq(Article::getStatus, 0);
        long pendingArticle = articleMapper.selectCount(pendingArticleWrapper);
        stats.put("pendingArticle", pendingArticle);
        
        LambdaQueryWrapper<ConsultOrder> processingOrderWrapper = new LambdaQueryWrapper<>();
        processingOrderWrapper.in(ConsultOrder::getStatus, 2, 3);
        long processingOrder = consultOrderMapper.selectCount(processingOrderWrapper);
        stats.put("processingOrder", processingOrder);
        
        List<Map<String, Object>> trendData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(formatter));
            
            LambdaQueryWrapper<SysUser> dayUserWrapper = new LambdaQueryWrapper<>();
            dayUserWrapper.ge(SysUser::getCreateTime, dayStart).le(SysUser::getCreateTime, dayEnd);
            dayData.put("userCount", sysUserMapper.selectCount(dayUserWrapper));
            
            LambdaQueryWrapper<ConsultOrder> dayConsultWrapper = new LambdaQueryWrapper<>();
            dayConsultWrapper.ge(ConsultOrder::getCreateTime, dayStart).le(ConsultOrder::getCreateTime, dayEnd);
            dayData.put("orderCount", consultOrderMapper.selectCount(dayConsultWrapper));
            
            trendData.add(dayData);
        }
        stats.put("trendData", trendData);
        
        return Result.success(stats);
    }
    
    @GetMapping("/recent-users")
    public Result<List<Map<String, Object>>> getRecentUsers() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime).last("LIMIT 10");
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("realName", user.getRealName());
            map.put("avatar", user.getAvatar());
            map.put("role", user.getRole());
            map.put("status", user.getStatus());
            map.put("createTime", user.getCreateTime());
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    @GetMapping("/recent-orders")
    public Result<List<Map<String, Object>>> getRecentOrders() {
        LambdaQueryWrapper<ConsultOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ConsultOrder::getCreateTime).last("LIMIT 10");
        List<ConsultOrder> orders = consultOrderMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("title", order.getTitle());
            map.put("status", order.getStatus());
            map.put("price", order.getPrice());
            map.put("payStatus", order.getPayStatus());
            map.put("createTime", order.getCreateTime());
            
            SysUser user = sysUserMapper.selectById(order.getUserId());
            map.put("userName", user != null ? user.getRealName() : "未知用户");
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    @GetMapping("/order-distribution")
    public Result<Map<String, Object>> getOrderDistribution() {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> statusDistribution = new ArrayList<>();
        String[] statusNames = {"待接单", "已接单", "进行中", "已完成", "已取消"};
        for (int i = 1; i <= 5; i++) {
            LambdaQueryWrapper<ConsultOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ConsultOrder::getStatus, i);
            long count = consultOrderMapper.selectCount(wrapper);
            Map<String, Object> item = new HashMap<>();
            item.put("name", statusNames[i - 1]);
            item.put("value", count);
            statusDistribution.add(item);
        }
        result.put("statusDistribution", statusDistribution);
        
        List<Map<String, Object>> typeDistribution = new ArrayList<>();
        String[] typeNames = {"图文咨询", "电话咨询", "视频咨询"};
        for (int i = 1; i <= 3; i++) {
            LambdaQueryWrapper<ConsultOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ConsultOrder::getConsultType, i);
            long count = consultOrderMapper.selectCount(wrapper);
            Map<String, Object> item = new HashMap<>();
            item.put("name", typeNames[i - 1]);
            item.put("value", count);
            typeDistribution.add(item);
        }
        result.put("typeDistribution", typeDistribution);
        
        return Result.success(result);
    }
}
