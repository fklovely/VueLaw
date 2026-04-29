package com.legal.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Article;
import com.legal.platform.mapper.ArticleMapper;
import com.legal.platform.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public PageResult<Article> getArticleList(Integer page, Integer size, String keyword, Long categoryId, Integer articleType) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Article::getTitle, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (articleType != null) {
            wrapper.eq(Article::getArticleType, articleType);
        }
        wrapper.orderByDesc(Article::getPublishTime);
        IPage<Article> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public PageResult<Article> getMyArticleList(Integer page, Integer size, Long authorId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getAuthorId, authorId);
        wrapper.orderByDesc(Article::getCreateTime);
        IPage<Article> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<Article> getArticleDetail(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        article.setViewCount(article.getViewCount() + 1);
        this.updateById(article);
        return Result.success(article);
    }

    @Override
    public Result<Boolean> addArticle(Article article, Long userId, String authorName) {
        article.setArticleNo("ART" + IdUtil.getSnowflakeNextIdStr());
        article.setAuthorId(userId);
        article.setAuthorName(authorName);
        article.setStatus(0);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setShareCount(0);
        this.save(article);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateArticle(Article article, Long userId) {
        Article existArticle = this.getById(article.getId());
        if (existArticle == null) {
            return Result.error("文章不存在");
        }
        if (!existArticle.getAuthorId().equals(userId)) {
            return Result.error("无权操作");
        }
        existArticle.setTitle(article.getTitle());
        existArticle.setCategoryId(article.getCategoryId());
        existArticle.setCoverImage(article.getCoverImage());
        existArticle.setSummary(article.getSummary());
        existArticle.setContent(article.getContent());
        existArticle.setArticleType(article.getArticleType());
        existArticle.setTags(article.getTags());
        this.updateById(existArticle);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteArticle(Long id, Long userId) {
        Article article = this.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            return Result.error("无权操作");
        }
        this.removeById(id);
        return Result.success("删除成功", true);
    }

    @Override
    public Result<Boolean> publishArticle(Long id, Long userId) {
        Article article = this.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            return Result.error("无权操作");
        }
        article.setStatus(0);
        article.setPublishTime(LocalDateTime.now());
        this.updateById(article);
        return Result.success("提交成功，请等待管理员审核", true);
    }

    @Override
    public Result<Boolean> auditArticle(Long id, Integer status) {
        Article article = this.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        article.setStatus(status);
        this.updateById(article);
        String msg = status == 1 ? "审核通过" : "审核拒绝";
        return Result.success(msg, true);
    }
}
