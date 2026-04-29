package com.legal.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalDocument;
import com.legal.platform.mapper.LegalDocumentMapper;
import com.legal.platform.service.LegalDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LegalDocumentServiceImpl extends ServiceImpl<LegalDocumentMapper, LegalDocument> implements LegalDocumentService {

    @Override
    public PageResult<LegalDocument> getDocumentList(Integer page, Integer size, String keyword, String docType, Integer isPremium) {
        LambdaQueryWrapper<LegalDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LegalDocument::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(LegalDocument::getName, keyword);
        }
        if (StringUtils.hasText(docType)) {
            wrapper.eq(LegalDocument::getDocType, docType);
        }
        if (isPremium != null) {
            wrapper.eq(LegalDocument::getIsPremium, isPremium);
        }
        wrapper.orderByDesc(LegalDocument::getDownloadCount);
        IPage<LegalDocument> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<LegalDocument> getDocumentDetail(Long id) {
        LegalDocument document = this.getById(id);
        if (document == null) {
            return Result.error("文书不存在");
        }
        return Result.success(document);
    }

    @Override
    public Result<Boolean> addDocument(LegalDocument document, Long userId) {
        document.setDocNo("DOC" + IdUtil.getSnowflakeNextIdStr());
        document.setCreateBy(userId);
        document.setStatus(1);
        document.setDownloadCount(0);
        this.save(document);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateDocument(LegalDocument document) {
        LegalDocument existDoc = this.getById(document.getId());
        if (existDoc == null) {
            return Result.error("文书不存在");
        }
        existDoc.setName(document.getName());
        existDoc.setDocType(document.getDocType());
        existDoc.setDescription(document.getDescription());
        existDoc.setContent(document.getContent());
        existDoc.setFileUrl(document.getFileUrl());
        existDoc.setPreviewUrl(document.getPreviewUrl());
        existDoc.setIsPremium(document.getIsPremium());
        existDoc.setPrice(document.getPrice());
        this.updateById(existDoc);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteDocument(Long id) {
        this.removeById(id);
        return Result.success("删除成功", true);
    }

    @Override
    public Result<Boolean> incrementDownload(Long id) {
        LegalDocument document = this.getById(id);
        if (document != null) {
            document.setDownloadCount(document.getDownloadCount() + 1);
            this.updateById(document);
        }
        return Result.success(true);
    }
}
