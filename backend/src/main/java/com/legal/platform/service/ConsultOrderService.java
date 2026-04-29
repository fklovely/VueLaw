package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultOrder;

import java.util.Map;

public interface ConsultOrderService extends IService<ConsultOrder> {
    PageResult<ConsultOrder> getOrderList(Integer page, Integer size, Long userId, Long lawyerId, Integer status, Integer consultType);
    Result<ConsultOrder> getOrderDetail(Long id);
    Result<Boolean> createOrder(ConsultOrder order, Long userId);
    Result<Boolean> acceptOrder(Long id, Long lawyerId);
    Result<Boolean> processOrder(Long id, String result);
    Result<Boolean> finishOrder(Long id);
    Result<Boolean> cancelOrder(Long id, Long userId);
    Result<Boolean> evaluateOrder(Long id, Integer score, String content);
    Result<Map<String, Object>> getOrderStatistics(Long userId, String role);
}
