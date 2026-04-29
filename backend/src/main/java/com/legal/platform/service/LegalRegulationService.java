package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalRegulation;

public interface LegalRegulationService extends IService<LegalRegulation> {
    PageResult<LegalRegulation> getRegulationList(Integer page, Integer size, String keyword, String regType);
    Result<LegalRegulation> getRegulationDetail(Long id);
    Result<Boolean> addRegulation(LegalRegulation regulation, Long userId);
    Result<Boolean> updateRegulation(LegalRegulation regulation);
    Result<Boolean> deleteRegulation(Long id);
}
