package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalCase;
import com.legal.platform.service.LegalCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/case")
public class CaseController {

    @Autowired
    private LegalCaseService legalCaseService;

    @GetMapping("/list")
    public Result<PageResult<LegalCase>> getCaseList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String caseType,
            @RequestParam(required = false) String province) {
        return Result.success(legalCaseService.getCaseList(page, size, keyword, caseType, province));
    }

    @GetMapping("/detail/{id}")
    public Result<LegalCase> getCaseDetail(@PathVariable Long id) {
        return legalCaseService.getCaseDetail(id);
    }

    @PostMapping
    public Result<Boolean> addCase(@RequestBody LegalCase legalCase, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return legalCaseService.addCase(legalCase, userId);
    }

    @PutMapping
    public Result<Boolean> updateCase(@RequestBody LegalCase legalCase) {
        return legalCaseService.updateCase(legalCase);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCase(@PathVariable Long id) {
        return legalCaseService.deleteCase(id);
    }
}
