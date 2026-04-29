package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sensitive_word")
public class SensitiveWord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String word;
    private String wordType;
    private Integer level;
    private String replaceWord;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
