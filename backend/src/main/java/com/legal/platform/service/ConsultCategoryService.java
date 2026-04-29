package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultCategory;

import java.util.List;

public interface ConsultCategoryService extends IService<ConsultCategory> {
    Result<List<ConsultCategory>> getCategoryList();
    Result<List<ConsultCategory>> getCategoryTree();
    Result<Boolean> addCategory(ConsultCategory category);
    Result<Boolean> updateCategory(ConsultCategory category);
    Result<Boolean> deleteCategory(Long id);
}
