package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String questionNo;
    private Long userId;
    private String title;
    private String content;
    private Long categoryId;
    private Integer anonymous;
    private Integer viewCount;
    private Integer answerCount;
    private Integer likeCount;
    private Integer status;
    private Integer isSolved;
    private Long bestAnswerId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
