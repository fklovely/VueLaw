package com.legal.platform.controller;

import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultCategory;
import com.legal.platform.service.ConsultCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ConsultCategoryService consultCategoryService;

    @GetMapping("/list")
    public Result<List<ConsultCategory>> getCategoryList() {
        return consultCategoryService.getCategoryList();
    }

    @GetMapping("/tree")
    public Result<List<ConsultCategory>> getCategoryTree() {
        return consultCategoryService.getCategoryTree();
    }

    @PostMapping
    public Result<Boolean> addCategory(@RequestBody ConsultCategory category) {
        return consultCategoryService.addCategory(category);
    }

    @PutMapping
    public Result<Boolean> updateCategory(@RequestBody ConsultCategory category) {
        return consultCategoryService.updateCategory(category);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        return consultCategoryService.deleteCategory(id);
    }
}
