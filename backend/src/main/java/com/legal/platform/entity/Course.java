package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseNo;
    private String title;
    private Long categoryId;
    private Long teacherId;
    private String teacherName;
    private String coverImage;
    private String description;
    private Integer courseType;
    private BigDecimal price;
    private Integer duration;
    private Integer chapterCount;
    private Integer studentCount;
    private BigDecimal rating;
    private Integer isFree;
    private String tags;
    private Integer viewCount;
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
