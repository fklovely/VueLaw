package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalDocument;
import com.legal.platform.service.LegalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private LegalDocumentService legalDocumentService;

    @GetMapping("/list")
    public Result<PageResult<LegalDocument>> getDocumentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String docType,
            @RequestParam(required = false) Integer isPremium) {
        return Result.success(legalDocumentService.getDocumentList(page, size, keyword, docType, isPremium));
    }

    @GetMapping("/detail/{id}")
    public Result<LegalDocument> getDocumentDetail(@PathVariable Long id) {
        return legalDocumentService.getDocumentDetail(id);
    }

    @PostMapping
    public Result<Boolean> addDocument(@RequestBody LegalDocument document, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return legalDocumentService.addDocument(document, userId);
    }

    @PutMapping
    public Result<Boolean> updateDocument(@RequestBody LegalDocument document) {
        return legalDocumentService.updateDocument(document);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDocument(@PathVariable Long id) {
        return legalDocumentService.deleteDocument(id);
    }

    @PostMapping("/download/{id}")
    public Result<Boolean> incrementDownload(@PathVariable Long id) {
        return legalDocumentService.incrementDownload(id);
    }
}
