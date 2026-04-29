package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String articleNo;
    private String title;
    private Long categoryId;
    private Long authorId;
    private String authorName;
    private String coverImage;
    private String summary;
    private String content;
    private Integer articleType;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Integer isRecommend;
    private Integer status;
    private LocalDateTime publishTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
