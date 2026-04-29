package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("consult_order")
public class ConsultOrder implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long lawyerId;
    private Long categoryId;
    private Integer consultType;
    private String title;
    private String content;
    private String attachments;
    private Integer status;
    private BigDecimal price;
    private Integer payStatus;
    private LocalDateTime payTime;
    private String payMethod;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String phone;
    private String result;
    private Integer evaluateStatus;
    private Integer evaluateScore;
    private String evaluateContent;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    @TableField(exist = false)
    private String payPassword;
}
