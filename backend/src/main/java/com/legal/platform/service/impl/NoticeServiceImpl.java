package com.legal.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Notice;
import com.legal.platform.mapper.NoticeMapper;
import com.legal.platform.service.NoticeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public PageResult<Notice> getNoticeList(Integer page, Integer size, String noticeType) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1);
        if (StringUtils.hasText(noticeType)) {
            wrapper.eq(Notice::getNoticeType, noticeType);
        }
        wrapper.orderByDesc(Notice::getIsTop);
        wrapper.orderByDesc(Notice::getPublishTime);
        IPage<Notice> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<Notice> getNoticeDetail(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            return Result.error("公告不存在");
        }
        notice.setViewCount(notice.getViewCount() + 1);
        this.updateById(notice);
        return Result.success(notice);
    }

    @Override
    public Result<Boolean> addNotice(Notice notice, Long userId) {
        notice.setCreateBy(userId);
        notice.setStatus(0);
        notice.setViewCount(0);
        this.save(notice);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateNotice(Notice notice) {
        Notice existNotice = this.getById(notice.getId());
        if (existNotice == null) {
            return Result.error("公告不存在");
        }
        existNotice.setTitle(notice.getTitle());
        existNotice.setContent(notice.getContent());
        existNotice.setNoticeType(notice.getNoticeType());
        existNotice.setCoverImage(notice.getCoverImage());
        existNotice.setIsTop(notice.getIsTop());
        this.updateById(existNotice);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteNotice(Long id) {
        this.removeById(id);
        return Result.success("删除成功", true);
    }

    @Override
    public Result<Boolean> publishNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            return Result.error("公告不存在");
        }
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        this.updateById(notice);
        return Result.success("发布成功", true);
    }
}
