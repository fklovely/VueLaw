package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("answer")
public class Answer implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long userId;
    private Integer userType;
    private String content;
    private Integer likeCount;
    private Integer isBest;
    private Integer isAdopted;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
