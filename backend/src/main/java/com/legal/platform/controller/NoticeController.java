package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Notice;
import com.legal.platform.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public Result<PageResult<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String noticeType) {
        return Result.success(noticeService.getNoticeList(page, size, noticeType));
    }

    @GetMapping("/detail/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        return noticeService.getNoticeDetail(id);
    }

    @PostMapping
    public Result<Boolean> addNotice(@RequestBody Notice notice, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return noticeService.addNotice(notice, userId);
    }

    @PutMapping
    public Result<Boolean> updateNotice(@RequestBody Notice notice) {
        return noticeService.updateNotice(notice);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteNotice(@PathVariable Long id) {
        return noticeService.deleteNotice(id);
    }

    @PostMapping("/publish/{id}")
    public Result<Boolean> publishNotice(@PathVariable Long id) {
        return noticeService.publishNotice(id);
    }
}
