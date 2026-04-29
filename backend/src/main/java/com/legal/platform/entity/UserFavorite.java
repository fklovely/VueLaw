package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
public class UserFavorite implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer favoriteType;
    private Long targetId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
