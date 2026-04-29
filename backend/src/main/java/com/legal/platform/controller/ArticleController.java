package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Article;
import com.legal.platform.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/list")
    public Result<PageResult<Article>> getArticleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer articleType) {
        return Result.success(articleService.getArticleList(page, size, keyword, categoryId, articleType));
    }

    @GetMapping("/my-list")
    public Result<PageResult<Article>> getMyArticleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long authorId = (Long) request.getAttribute("userId");
        return Result.success(articleService.getMyArticleList(page, size, authorId));
    }

    @GetMapping("/detail/{id}")
    public Result<Article> getArticleDetail(@PathVariable Long id) {
        return articleService.getArticleDetail(id);
    }

    @PostMapping
    public Result<Boolean> addArticle(@RequestBody Article article, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String authorName = (String) request.getAttribute("realName");
        return articleService.addArticle(article, userId, authorName);
    }

    @PutMapping
    public Result<Boolean> updateArticle(@RequestBody Article article, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return articleService.updateArticle(article, userId);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return articleService.deleteArticle(id, userId);
    }

    @PostMapping("/publish/{id}")
    public Result<Boolean> publishArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return articleService.publishArticle(id, userId);
    }

    @PutMapping("/audit/{id}")
    public Result<Boolean> auditArticle(@PathVariable Long id, @RequestParam Integer status) {
        return articleService.auditArticle(id, status);
    }
}
