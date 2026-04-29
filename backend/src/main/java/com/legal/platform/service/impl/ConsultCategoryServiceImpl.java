package com.legal.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultCategory;
import com.legal.platform.mapper.ConsultCategoryMapper;
import com.legal.platform.service.ConsultCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultCategoryServiceImpl extends ServiceImpl<ConsultCategoryMapper, ConsultCategory> implements ConsultCategoryService {

    @Override
    public Result<List<ConsultCategory>> getCategoryList() {
        LambdaQueryWrapper<ConsultCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsultCategory::getStatus, 1);
        wrapper.orderByAsc(ConsultCategory::getSortOrder);
        List<ConsultCategory> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<List<ConsultCategory>> getCategoryTree() {
        LambdaQueryWrapper<ConsultCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsultCategory::getStatus, 1);
        wrapper.orderByAsc(ConsultCategory::getSortOrder);
        List<ConsultCategory> allCategories = this.list(wrapper);
        List<ConsultCategory> rootCategories = allCategories.stream()
                .filter(c -> c.getParentId() == 0)
                .collect(Collectors.toList());
        for (ConsultCategory root : rootCategories) {
            List<ConsultCategory> children = allCategories.stream()
                    .filter(c -> c.getParentId().equals(root.getId()))
                    .collect(Collectors.toList());
        }
        return Result.success(rootCategories);
    }

    @Override
    public Result<Boolean> addCategory(ConsultCategory category) {
        category.setStatus(1);
        this.save(category);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateCategory(ConsultCategory category) {
        ConsultCategory existCategory = this.getById(category.getId());
        if (existCategory == null) {
            return Result.error("分类不存在");
        }
        existCategory.setName(category.getName());
        existCategory.setParentId(category.getParentId());
        existCategory.setLevel(category.getLevel());
        existCategory.setIcon(category.getIcon());
        existCategory.setSortOrder(category.getSortOrder());
        this.updateById(existCategory);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteCategory(Long id) {
        this.removeById(id);
        return Result.success("删除成功", true);
    }
}
