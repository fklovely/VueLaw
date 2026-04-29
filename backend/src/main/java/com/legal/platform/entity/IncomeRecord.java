package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("income_record")
public class IncomeRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String recordNo;
    private Long lawyerId;
    private Long orderId;
    private Integer orderType;
    private BigDecimal orderAmount;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private BigDecimal incomeAmount;
    private Integer status;
    private LocalDateTime settleTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
