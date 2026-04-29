package com.legal.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalCase;
import com.legal.platform.mapper.LegalCaseMapper;
import com.legal.platform.service.LegalCaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LegalCaseServiceImpl extends ServiceImpl<LegalCaseMapper, LegalCase> implements LegalCaseService {

    @Override
    public PageResult<LegalCase> getCaseList(Integer page, Integer size, String keyword, String caseType, String province) {
        LambdaQueryWrapper<LegalCase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LegalCase::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(LegalCase::getTitle, keyword);
        }
        if (StringUtils.hasText(caseType)) {
            wrapper.eq(LegalCase::getCaseType, caseType);
        }
        if (StringUtils.hasText(province)) {
            wrapper.eq(LegalCase::getProvince, province);
        }
        wrapper.orderByDesc(LegalCase::getJudgeDate);
        IPage<LegalCase> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<LegalCase> getCaseDetail(Long id) {
        LegalCase legalCase = this.getById(id);
        if (legalCase == null) {
            return Result.error("案例不存在");
        }
        legalCase.setViewCount(legalCase.getViewCount() + 1);
        this.updateById(legalCase);
        return Result.success(legalCase);
    }

    @Override
    public Result<Boolean> addCase(LegalCase legalCase, Long userId) {
        legalCase.setCaseNo("CASE" + IdUtil.getSnowflakeNextIdStr());
        legalCase.setCreateBy(userId);
        legalCase.setStatus(1);
        legalCase.setViewCount(0);
        legalCase.setFavoriteCount(0);
        this.save(legalCase);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateCase(LegalCase legalCase) {
        LegalCase existCase = this.getById(legalCase.getId());
        if (existCase == null) {
            return Result.error("案例不存在");
        }
        existCase.setTitle(legalCase.getTitle());
        existCase.setCaseType(legalCase.getCaseType());
        existCase.setCourtName(legalCase.getCourtName());
        existCase.setCaseNoInner(legalCase.getCaseNoInner());
        existCase.setJudgeDate(legalCase.getJudgeDate());
        existCase.setProvince(legalCase.getProvince());
        existCase.setCity(legalCase.getCity());
        existCase.setCaseAmount(legalCase.getCaseAmount());
        existCase.setCaseResult(legalCase.getCaseResult());
        existCase.setSummary(legalCase.getSummary());
        existCase.setContent(legalCase.getContent());
        existCase.setKeyPoints(legalCase.getKeyPoints());
        existCase.setLegalBasis(legalCase.getLegalBasis());
        existCase.setRelatedArticles(legalCase.getRelatedArticles());
        existCase.setTags(legalCase.getTags());
        this.updateById(existCase);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteCase(Long id) {
        this.removeById(id);
        return Result.success("删除成功", true);
    }
}
