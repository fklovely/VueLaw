package com.legal.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.LegalRegulation;
import com.legal.platform.mapper.LegalRegulationMapper;
import com.legal.platform.service.LegalRegulationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LegalRegulationServiceImpl extends ServiceImpl<LegalRegulationMapper, LegalRegulation> implements LegalRegulationService {

    @Override
    public PageResult<LegalRegulation> getRegulationList(Integer page, Integer size, String keyword, String regType) {
        LambdaQueryWrapper<LegalRegulation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LegalRegulation::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(LegalRegulation::getTitle, keyword);
        }
        if (StringUtils.hasText(regType)) {
            wrapper.eq(LegalRegulation::getRegType, regType);
        }
        wrapper.orderByDesc(LegalRegulation::getIssueDate);
        IPage<LegalRegulation> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<LegalRegulation> getRegulationDetail(Long id) {
        LegalRegulation regulation = this.getById(id);
        if (regulation == null) {
            return Result.error("法规不存在");
        }
        regulation.setViewCount(regulation.getViewCount() + 1);
        this.updateById(regulation);
        return Result.success(regulation);
    }

    @Override
    public Result<Boolean> addRegulation(LegalRegulation regulation, Long userId) {
        regulation.setRegNo("REG" + IdUtil.getSnowflakeNextIdStr());
        regulation.setCreateBy(userId);
        regulation.setStatus(1);
        regulation.setViewCount(0);
        this.save(regulation);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateRegulation(LegalRegulation regulation) {
        LegalRegulation existReg = this.getById(regulation.getId());
        if (existReg == null) {
            return Result.error("法规不存在");
        }
        existReg.setTitle(regulation.getTitle());
        existReg.setRegType(regulation.getRegType());
        existReg.setIssueOrg(regulation.getIssueOrg());
        existReg.setIssueDate(regulation.getIssueDate());
        existReg.setEffectiveDate(regulation.getEffectiveDate());
        existReg.setProvince(regulation.getProvince());
        existReg.setContent(regulation.getContent());
        existReg.setSummary(regulation.getSummary());
        existReg.setInterpretation(regulation.getInterpretation());
        existReg.setRelatedCases(regulation.getRelatedCases());
        existReg.setRelatedRegs(regulation.getRelatedRegs());
        existReg.setKeywords(regulation.getKeywords());
        this.updateById(existReg);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteRegulation(Long id) {
        this.removeById(id);
        return Result.success("删除成功", true);
    }
}
