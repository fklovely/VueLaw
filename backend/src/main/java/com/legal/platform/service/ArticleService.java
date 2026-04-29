package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Article;

public interface ArticleService extends IService<Article> {
    PageResult<Article> getArticleList(Integer page, Integer size, String keyword, Long categoryId, Integer articleType);
    PageResult<Article> getMyArticleList(Integer page, Integer size, Long authorId);
    Result<Article> getArticleDetail(Long id);
    Result<Boolean> addArticle(Article article, Long userId, String authorName);
    Result<Boolean> updateArticle(Article article, Long userId);
    Result<Boolean> deleteArticle(Long id, Long userId);
    Result<Boolean> publishArticle(Long id, Long userId);
    Result<Boolean> auditArticle(Long id, Integer status);
}
