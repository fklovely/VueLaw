package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("lawyer_follow")
public class LawyerFollow implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long lawyerId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
