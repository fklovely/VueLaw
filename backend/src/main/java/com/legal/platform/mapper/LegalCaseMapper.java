package com.legal.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.legal.platform.entity.LegalCase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LegalCaseMapper extends BaseMapper<LegalCase> {
}
