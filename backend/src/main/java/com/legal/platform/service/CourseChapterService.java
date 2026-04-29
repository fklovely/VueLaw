package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.CourseChapter;

import java.util.List;

public interface CourseChapterService extends IService<CourseChapter> {
    Result<List<CourseChapter>> getChapterListByCourseId(Long courseId);
    Result<Boolean> addChapter(CourseChapter chapter, Long userId);
    Result<Boolean> updateChapter(CourseChapter chapter, Long userId);
    Result<Boolean> deleteChapter(Long id, Long userId);
}
