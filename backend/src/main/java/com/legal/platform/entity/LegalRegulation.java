package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("legal_regulation")
public class LegalRegulation implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String regNo;
    private String title;
    private String regType;
    private String issueOrg;
    private LocalDate issueDate;
    private LocalDate effectiveDate;
    private String province;
    private String content;
    private String summary;
    private String interpretation;
    private String relatedCases;
    private String relatedRegs;
    private String keywords;
    private Integer viewCount;
    private Integer isRecommend;
    private Integer status;
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
