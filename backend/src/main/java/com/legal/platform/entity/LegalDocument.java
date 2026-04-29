package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("legal_document")
public class LegalDocument implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String docNo;
    private Long categoryId;
    private String name;
    private String docType;
    private String description;
    private String content;
    private String fileUrl;
    private String previewUrl;
    private Integer downloadCount;
    private Integer isTemplate;
    private Integer isPremium;
    private BigDecimal price;
    private Integer status;
    private Integer sortOrder;
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
