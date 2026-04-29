package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("service_evaluation")
public class ServiceEvaluation implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Integer orderType;
    private Long userId;
    private Long lawyerId;
    private Integer professionalScore;
    private Integer responseScore;
    private Integer attitudeScore;
    private Integer totalScore;
    private String content;
    private String images;
    private String reply;
    private LocalDateTime replyTime;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String orderTitle;
}
