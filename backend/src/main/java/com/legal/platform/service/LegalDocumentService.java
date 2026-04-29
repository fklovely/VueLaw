package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalDocument;

public interface LegalDocumentService extends IService<LegalDocument> {
    PageResult<LegalDocument> getDocumentList(Integer page, Integer size, String keyword, String docType, Integer isPremium);
    Result<LegalDocument> getDocumentDetail(Long id);
    Result<Boolean> addDocument(LegalDocument document, Long userId);
    Result<Boolean> updateDocument(LegalDocument document);
    Result<Boolean> deleteDocument(Long id);
    Result<Boolean> incrementDownload(Long id);
}
