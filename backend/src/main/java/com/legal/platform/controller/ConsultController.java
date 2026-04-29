package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultOrder;
import com.legal.platform.service.ConsultOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/consult")
public class ConsultController {

    @Autowired
    private ConsultOrderService consultOrderService;

    @GetMapping("/list")
    public Result<PageResult<ConsultOrder>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer consultType,
            HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long currentUserId = (Long) request.getAttribute("userId");
        if ("client".equals(role) && userId == null) {
            userId = currentUserId;
        } else if ("lawyer".equals(role) && lawyerId == null) {
            lawyerId = currentUserId;
        }
        return Result.success(consultOrderService.getOrderList(page, size, userId, lawyerId, status, consultType));
    }

    @GetMapping("/detail/{id}")
    public Result<ConsultOrder> getOrderDetail(@PathVariable Long id) {
        return consultOrderService.getOrderDetail(id);
    }

    @PostMapping
    public Result<Boolean> createOrder(@RequestBody ConsultOrder order, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return consultOrderService.createOrder(order, userId);
    }

    @PostMapping("/accept/{id}")
    public Result<Boolean> acceptOrder(@PathVariable Long id, HttpServletRequest request) {
        Long lawyerId = (Long) request.getAttribute("userId");
        return consultOrderService.acceptOrder(id, lawyerId);
    }

    @PostMapping("/process/{id}")
    public Result<Boolean> processOrder(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String result = params.get("result");
        return consultOrderService.processOrder(id, result);
    }

    @PostMapping("/finish/{id}")
    public Result<Boolean> finishOrder(@PathVariable Long id) {
        return consultOrderService.finishOrder(id);
    }

    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return consultOrderService.cancelOrder(id, userId);
    }

    @PostMapping("/evaluate/{id}")
    public Result<Boolean> evaluateOrder(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer score = Integer.parseInt(params.get("score").toString());
        String content = (String) params.get("content");
        return consultOrderService.evaluateOrder(id, score, content);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        return consultOrderService.getOrderStatistics(userId, role);
    }
}
