package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_chapter")
public class CourseChapter implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String title;
    private Integer chapterNo;
    private String videoUrl;
    private Integer duration;
    private String description;
    private Integer isFree;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
