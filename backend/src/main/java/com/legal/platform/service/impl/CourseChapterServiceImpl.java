package com.legal.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Course;
import com.legal.platform.entity.CourseChapter;
import com.legal.platform.mapper.CourseChapterMapper;
import com.legal.platform.mapper.CourseMapper;
import com.legal.platform.service.CourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements CourseChapterService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Result<List<CourseChapter>> getChapterListByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseChapter::getCourseId, courseId);
        wrapper.orderByAsc(CourseChapter::getSortOrder);
        wrapper.orderByAsc(CourseChapter::getChapterNo);
        List<CourseChapter> chapters = this.list(wrapper);
        return Result.success(chapters);
    }

    @Override
    public Result<Boolean> addChapter(CourseChapter chapter, Long userId) {
        Course course = courseMapper.selectById(chapter.getCourseId());
        if (course == null) {
            return Result.error("课程不存在");
        }
        if (!course.getTeacherId().equals(userId)) {
            return Result.error("无权操作");
        }
        this.save(chapter);
        course.setChapterCount(course.getChapterCount() + 1);
        courseMapper.updateById(course);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateChapter(CourseChapter chapter, Long userId) {
        CourseChapter existChapter = this.getById(chapter.getId());
        if (existChapter == null) {
            return Result.error("章节不存在");
        }
        Course course = courseMapper.selectById(existChapter.getCourseId());
        if (course == null) {
            return Result.error("课程不存在");
        }
        if (!course.getTeacherId().equals(userId)) {
            return Result.error("无权操作");
        }
        existChapter.setTitle(chapter.getTitle());
        existChapter.setVideoUrl(chapter.getVideoUrl());
        existChapter.setDuration(chapter.getDuration());
        existChapter.setIsFree(chapter.getIsFree());
        existChapter.setSortOrder(chapter.getSortOrder());
        this.updateById(existChapter);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteChapter(Long id, Long userId) {
        CourseChapter chapter = this.getById(id);
        if (chapter == null) {
            return Result.error("章节不存在");
        }
        Course course = courseMapper.selectById(chapter.getCourseId());
        if (course == null) {
            return Result.error("课程不存在");
        }
        if (!course.getTeacherId().equals(userId)) {
            return Result.error("无权操作");
        }
        this.removeById(id);
        course.setChapterCount(Math.max(0, course.getChapterCount() - 1));
        courseMapper.updateById(course);
        return Result.success("删除成功", true);
    }
}
