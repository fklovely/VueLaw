package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Notice;

public interface NoticeService extends IService<Notice> {
    PageResult<Notice> getNoticeList(Integer page, Integer size, String noticeType);
    Result<Notice> getNoticeDetail(Long id);
    Result<Boolean> addNotice(Notice notice, Long userId);
    Result<Boolean> updateNotice(Notice notice);
    Result<Boolean> deleteNotice(Long id);
    Result<Boolean> publishNotice(Long id);
}
