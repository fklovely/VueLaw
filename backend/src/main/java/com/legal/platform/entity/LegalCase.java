package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("legal_case")
public class LegalCase implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String caseNo;
    private String title;
    private String caseType;
    private String courtName;
    private String caseNoInner;
    private LocalDate judgeDate;
    private String province;
    private String city;
    private BigDecimal caseAmount;
    private String caseResult;
    private String summary;
    private String content;
    private String keyPoints;
    private String legalBasis;
    private String relatedArticles;
    private String tags;
    private Integer viewCount;
    private Integer favoriteCount;
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
