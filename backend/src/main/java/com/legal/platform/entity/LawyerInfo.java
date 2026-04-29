package com.legal.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("lawyer_info")
public class LawyerInfo implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String lawyerName;
    private String lawyerNo;
    private String idCard;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer age;
    private String province;
    private String city;
    private String lawFirm;
    private String education;
    private String professionalTitle;
    private String profile;
    private String expertises;
    private BigDecimal consultPrice;
    private BigDecimal deepConsultPrice;
    private BigDecimal documentPrice;
    private BigDecimal auditPrice;
    private BigDecimal videoPrice;
    private Integer caseCount;
    private BigDecimal rating;
    private Integer serviceCount;
    private String certificates;
    private String honors;
    private Integer status;
    private String rejectReason;
    private Integer isActive;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
