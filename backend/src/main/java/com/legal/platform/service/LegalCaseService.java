package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalCase;

public interface LegalCaseService extends IService<LegalCase> {
    PageResult<LegalCase> getCaseList(Integer page, Integer size, String keyword, String caseType, String province);
    Result<LegalCase> getCaseDetail(Long id);
    Result<Boolean> addCase(LegalCase legalCase, Long userId);
    Result<Boolean> updateCase(LegalCase legalCase);
    Result<Boolean> deleteCase(Long id);
}
