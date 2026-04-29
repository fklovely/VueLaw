package com.legal.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultOrder;
import com.legal.platform.entity.DocumentOrder;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.entity.OrderProgress;
import com.legal.platform.entity.SysUser;
import com.legal.platform.mapper.ConsultOrderMapper;
import com.legal.platform.mapper.DocumentOrderMapper;
import com.legal.platform.mapper.LawyerInfoMapper;
import com.legal.platform.mapper.OrderProgressMapper;
import com.legal.platform.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/order-progress")
public class OrderProgressController {

    private static final int ORDER_TYPE_CONSULT = 1;
    private static final int ORDER_TYPE_DOCUMENT = 2;

    @Autowired
    private ConsultOrderMapper consultOrderMapper;

    @Autowired
    private DocumentOrderMapper documentOrderMapper;

    @Autowired
    private LawyerInfoMapper lawyerInfoMapper;

    @Autowired
    private OrderProgressMapper orderProgressMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    private volatile boolean progressTableAvailable = true;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }

        List<Map<String, Object>> result = new ArrayList<>();

        LambdaQueryWrapper<ConsultOrder> consultWrapper = new LambdaQueryWrapper<>();
        consultWrapper.eq(ConsultOrder::getLawyerId, userId)
                .in(ConsultOrder::getStatus, 1, 2, 3, 4)
                .orderByDesc(ConsultOrder::getCreateTime);
        List<ConsultOrder> consultOrders = consultOrderMapper.selectList(consultWrapper);
        for (ConsultOrder order : consultOrders) {
            result.add(toConsultProgressRow(order));
        }

        Set<Long> documentLawyerIds = new HashSet<>();
        LambdaQueryWrapper<LawyerInfo> lawyerWrapper = new LambdaQueryWrapper<>();
        lawyerWrapper.eq(LawyerInfo::getUserId, userId);
        LawyerInfo lawyerInfo = lawyerInfoMapper.selectOne(lawyerWrapper);
        if (lawyerInfo != null && lawyerInfo.getId() != null) {
            documentLawyerIds.add(lawyerInfo.getId());
        }
        documentLawyerIds.add(userId);

        LambdaQueryWrapper<DocumentOrder> documentWrapper = new LambdaQueryWrapper<>();
        documentWrapper.in(DocumentOrder::getLawyerId, documentLawyerIds)
                .in(DocumentOrder::getStatus, 1, 2, 3)
                .orderByDesc(DocumentOrder::getCreateTime);
        List<DocumentOrder> documentOrders = documentOrderMapper.selectList(documentWrapper);
        Set<Long> addedDocumentIds = new HashSet<>();
        for (DocumentOrder order : documentOrders) {
            if (addedDocumentIds.add(order.getId())) {
                result.add(toDocumentProgressRow(order));
            }
        }

        result.sort((a, b) -> {
            Object aTime = a.get("orderCreateTime");
            Object bTime = b.get("orderCreateTime");
            if (aTime == null && bTime == null) {
                return 0;
            }
            if (aTime == null) {
                return 1;
            }
            if (bTime == null) {
                return -1;
            }
            return bTime.toString().compareTo(aTime.toString());
        });

        return Result.success(result);
    }

    @PostMapping("/update")
    @Transactional
    public Result<Boolean> update(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }

        Integer orderType = parseInteger(data.get("orderType"));
        Long orderId = parseLong(data.get("orderId"));
        Integer progress = parseInteger(data.get("progress"));
        String remark = data.get("remark") == null ? "" : data.get("remark").toString().trim();

        if (orderType == null || orderId == null || progress == null) {
            return Result.error("参数不完整");
        }
        if (progress < 0 || progress > 100) {
            return Result.error("进度必须在0到100之间");
        }

        Result<Boolean> permissionResult = validateCanUpdate(userId, orderType, orderId);
        if (permissionResult.getCode() != 200) {
            return permissionResult;
        }

        try {
            OrderProgress orderProgress = getProgressRecord(orderType, orderId);
            if (!progressTableAvailable) {
                return Result.error("数据库缺少订单进度表，请先执行 database/migrations/20260429_add_order_progress.sql 后重启后端");
            }
            if (orderProgress == null) {
                orderProgress = new OrderProgress();
                orderProgress.setOrderType(orderType);
                orderProgress.setOrderId(orderId);
                orderProgress.setProgress(progress);
                orderProgress.setRemark(remark);
                orderProgressMapper.insert(orderProgress);
            } else {
                orderProgress.setProgress(progress);
                orderProgress.setRemark(remark);
                orderProgress.setUpdateTime(LocalDateTime.now());
                orderProgressMapper.updateById(orderProgress);
            }
        } catch (RuntimeException e) {
            if (isMissingProgressTable(e)) {
                progressTableAvailable = false;
                return Result.error("数据库缺少订单进度表，请先执行 database/migrations/20260429_add_order_progress.sql 后重启后端");
            }
            throw e;
        }

        return Result.success("进度更新成功", true);
    }

    private Result<Boolean> validateCanUpdate(Long userId, Integer orderType, Long orderId) {
        if (ORDER_TYPE_CONSULT == orderType) {
            ConsultOrder order = consultOrderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (!userId.equals(order.getLawyerId())) {
                return Result.error("无权操作此订单");
            }
            if (order.getStatus() == null || order.getStatus() == 1) {
                return Result.error("请先接单后再更新进度");
            }
            if (order.getStatus() >= 4) {
                return Result.error("订单已完成或已取消，不能更新进度");
            }
            return Result.success(true);
        }

        if (ORDER_TYPE_DOCUMENT == orderType) {
            DocumentOrder order = documentOrderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (!isCurrentLawyerDocumentOrder(userId, order)) {
                return Result.error("无权操作此订单");
            }
            if (order.getStatus() == null || order.getStatus() == 1) {
                return Result.error("请先接单后再更新进度");
            }
            if (order.getStatus() >= 3) {
                return Result.error("订单已完成或已取消，不能更新进度");
            }
            return Result.success(true);
        }

        return Result.error("不支持的订单类型");
    }

    private boolean isCurrentLawyerDocumentOrder(Long userId, DocumentOrder order) {
        if (order.getLawyerId() == null) {
            return false;
        }
        if (userId.equals(order.getLawyerId())) {
            return true;
        }
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerInfo::getUserId, userId);
        LawyerInfo lawyerInfo = lawyerInfoMapper.selectOne(wrapper);
        return lawyerInfo != null && order.getLawyerId().equals(lawyerInfo.getId());
    }

    private Map<String, Object> toConsultProgressRow(ConsultOrder order) {
        Map<String, Object> row = baseRow(ORDER_TYPE_CONSULT, order.getId(), order.getStatus());
        row.put("orderNo", order.getOrderNo());
        row.put("orderTypeName", "咨询订单");
        row.put("title", order.getTitle());
        row.put("clientName", getClientName(order.getUserId()));
        row.put("statusLabel", getConsultStatusLabel(order.getStatus()));
        row.put("orderCreateTime", order.getCreateTime());
        fillProgress(row, order.getStatus(), getDefaultConsultProgress(order.getStatus()));
        return row;
    }

    private Map<String, Object> toDocumentProgressRow(DocumentOrder order) {
        Map<String, Object> row = baseRow(ORDER_TYPE_DOCUMENT, order.getId(), order.getStatus());
        row.put("orderNo", order.getOrderNo());
        row.put("orderTypeName", getDocumentTypeName(order.getOrderType()));
        row.put("title", order.getTitle());
        row.put("clientName", getClientName(order.getUserId()));
        row.put("statusLabel", getDocumentStatusLabel(order.getStatus()));
        row.put("orderCreateTime", order.getCreateTime());
        fillProgress(row, order.getStatus(), getDefaultDocumentProgress(order.getStatus()));
        return row;
    }

    private Map<String, Object> baseRow(Integer orderType, Long orderId, Integer status) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", orderType + "-" + orderId);
        row.put("orderType", orderType);
        row.put("orderId", orderId);
        row.put("status", status);
        return row;
    }

    private void fillProgress(Map<String, Object> row, Integer status, Integer defaultProgress) {
        Integer orderType = (Integer) row.get("orderType");
        Long orderId = (Long) row.get("orderId");
        OrderProgress progress = getProgressRecord(orderType, orderId);
        if (progress == null) {
            row.put("currentProgress", defaultProgress);
            row.put("remark", "");
            row.put("updateTime", null);
        } else {
            row.put("currentProgress", normalizeProgress(progress.getProgress()));
            row.put("remark", progress.getRemark());
            row.put("updateTime", progress.getUpdateTime());
        }
        if (isCompleted(orderType, status)) {
            row.put("currentProgress", 100);
            row.put("canUpdate", false);
        } else {
            row.put("canUpdate", canUpdate(orderType, status));
        }
    }

    private OrderProgress getProgressRecord(Integer orderType, Long orderId) {
        if (!progressTableAvailable) {
            return null;
        }
        LambdaQueryWrapper<OrderProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderProgress::getOrderType, orderType)
                .eq(OrderProgress::getOrderId, orderId)
                .last("LIMIT 1");
        try {
            return orderProgressMapper.selectOne(wrapper);
        } catch (RuntimeException e) {
            if (isMissingProgressTable(e)) {
                progressTableAvailable = false;
                return null;
            }
            throw e;
        }
    }

    private boolean isMissingProgressTable(RuntimeException e) {
        Throwable current = e;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                String normalized = message.toLowerCase();
                if (normalized.contains("order_progress")
                        && (normalized.contains("doesn't exist")
                        || normalized.contains("does not exist")
                        || normalized.contains("base table or view not found")
                        || normalized.contains("不存在"))) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private String getClientName(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return "用户" + userId;
        }
        if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            return user.getRealName();
        }
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            return user.getUsername();
        }
        return "用户" + userId;
    }

    private Integer getDefaultConsultProgress(Integer status) {
        if (status == null) {
            return 0;
        }
        switch (status) {
            case 1:
                return 20;
            case 2:
                return 50;
            case 3:
                return 80;
            case 4:
                return 100;
            default:
                return 0;
        }
    }

    private Integer getDefaultDocumentProgress(Integer status) {
        if (status == null) {
            return 0;
        }
        switch (status) {
            case 1:
                return 20;
            case 2:
                return 60;
            case 3:
                return 100;
            default:
                return 0;
        }
    }

    private boolean canUpdate(Integer orderType, Integer status) {
        if (status == null) {
            return false;
        }
        if (ORDER_TYPE_CONSULT == orderType) {
            return status == 2 || status == 3;
        }
        if (ORDER_TYPE_DOCUMENT == orderType) {
            return status == 2;
        }
        return false;
    }

    private boolean isCompleted(Integer orderType, Integer status) {
        if (status == null) {
            return false;
        }
        if (ORDER_TYPE_CONSULT == orderType) {
            return status == 4;
        }
        if (ORDER_TYPE_DOCUMENT == orderType) {
            return status == 3;
        }
        return false;
    }

    private Integer normalizeProgress(Integer progress) {
        if (progress == null) {
            return 0;
        }
        if (progress < 0) {
            return 0;
        }
        return progress > 100 ? 100 : progress;
    }

    private String getConsultStatusLabel(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "待接单";
            case 2:
                return "已接单";
            case 3:
                return "处理中";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知";
        }
    }

    private String getDocumentStatusLabel(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "待接单";
            case 2:
                return "处理中";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知";
        }
    }

    private String getDocumentTypeName(Integer orderType) {
        if (orderType == null) {
            return "文书服务";
        }
        return orderType == 1 ? "文书代写" : "文书审核";
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
