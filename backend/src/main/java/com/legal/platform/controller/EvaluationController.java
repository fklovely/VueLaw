package com.legal.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultOrder;
import com.legal.platform.entity.DocumentOrder;
import com.legal.platform.entity.ServiceEvaluation;
import com.legal.platform.entity.SysUser;
import com.legal.platform.mapper.ConsultOrderMapper;
import com.legal.platform.mapper.DocumentOrderMapper;
import com.legal.platform.mapper.ServiceEvaluationMapper;
import com.legal.platform.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private ServiceEvaluationMapper evaluationMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ConsultOrderMapper consultOrderMapper;

    @Autowired
    private DocumentOrderMapper documentOrderMapper;

    @GetMapping("/list")
    public Result<PageResult<ServiceEvaluation>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer score,
            HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        
        LambdaQueryWrapper<ServiceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceEvaluation::getLawyerId, lawyerId);
        if (score != null) {
            wrapper.eq(ServiceEvaluation::getTotalScore, score);
        }
        wrapper.orderByDesc(ServiceEvaluation::getCreateTime);
        
        IPage<ServiceEvaluation> pageResult = evaluationMapper.selectPage(new Page<>(page, size), wrapper);
        
        pageResult.getRecords().forEach(evaluation -> {
            SysUser user = sysUserMapper.selectById(evaluation.getUserId());
            if (user != null) {
                evaluation.setUserName(user.getRealName());
            }
            if (evaluation.getOrderType() != null && evaluation.getOrderType() == 2) {
                DocumentOrder docOrder = documentOrderMapper.selectById(evaluation.getOrderId());
                if (docOrder != null) {
                    evaluation.setOrderTitle(docOrder.getTitle());
                }
            } else {
                ConsultOrder order = consultOrderMapper.selectById(evaluation.getOrderId());
                if (order != null) {
                    evaluation.setOrderTitle(order.getTitle());
                }
            }
        });
        
        return Result.success(new PageResult<>(pageResult.getRecords(), pageResult.getTotal()));
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        
        LambdaQueryWrapper<ServiceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceEvaluation::getLawyerId, lawyerId);
        
        List<ServiceEvaluation> allEvaluations = evaluationMapper.selectList(wrapper);
        
        double avgScore = allEvaluations.stream()
                .mapToInt(ServiceEvaluation::getTotalScore)
                .average()
                .orElse(5.0);
        
        long totalCount = allEvaluations.size();
        long fiveStarCount = allEvaluations.stream()
                .filter(e -> e.getTotalScore() == 5)
                .count();
        long goodCount = allEvaluations.stream()
                .filter(e -> e.getTotalScore() >= 4)
                .count();
        
        int goodRate = totalCount > 0 ? (int) (goodCount * 100 / totalCount) : 100;
        
        Map<String, Object> result = new HashMap<>();
        result.put("avgScore", Math.round(avgScore * 10) / 10.0);
        result.put("totalCount", totalCount);
        result.put("fiveStarCount", fiveStarCount);
        result.put("goodRate", goodRate);
        
        return Result.success(result);
    }

    @PostMapping("/reply/{id}")
    public Result<String> reply(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        
        ServiceEvaluation evaluation = evaluationMapper.selectById(id);
        if (evaluation == null) {
            return Result.error("评价不存在");
        }
        if (!evaluation.getLawyerId().equals(lawyerId)) {
            return Result.error("无权操作");
        }
        
        evaluation.setReply(params.get("reply"));
        evaluation.setReplyTime(java.time.LocalDateTime.now());
        evaluationMapper.updateById(evaluation);
        
        return Result.success("回复成功", null);
    }
}
