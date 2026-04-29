package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalRegulation;
import com.legal.platform.service.LegalRegulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/regulation")
public class RegulationController {

    @Autowired
    private LegalRegulationService legalRegulationService;

    @GetMapping("/list")
    public Result<PageResult<LegalRegulation>> getRegulationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String regType) {
        return Result.success(legalRegulationService.getRegulationList(page, size, keyword, regType));
    }

    @GetMapping("/detail/{id}")
    public Result<LegalRegulation> getRegulationDetail(@PathVariable Long id) {
        return legalRegulationService.getRegulationDetail(id);
    }

    @PostMapping
    public Result<Boolean> addRegulation(@RequestBody LegalRegulation regulation, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return legalRegulationService.addRegulation(regulation, userId);
    }

    @PutMapping
    public Result<Boolean> updateRegulation(@RequestBody LegalRegulation regulation) {
        return legalRegulationService.updateRegulation(regulation);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRegulation(@PathVariable Long id) {
        return legalRegulationService.deleteRegulation(id);
    }
}
