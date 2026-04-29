package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("notice")
public class Notice implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String noticeType;
    private String coverImage;
    private Integer isTop;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime publishTime;
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
