package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> {
    PageResult<Course> getCourseList(Integer page, Integer size, String keyword, Long categoryId, Integer isFree);
    PageResult<Course> getMyCourseList(Integer page, Integer size, Long teacherId);
    Result<Course> getCourseDetail(Long id);
    Result<Boolean> addCourse(Course course, Long userId, String teacherName);
    Result<Boolean> updateCourse(Course course, Long userId);
    Result<Boolean> deleteCourse(Long id, Long userId);
    Result<Boolean> publishCourse(Long id, Long userId);
    Result<Boolean> auditCourse(Long id, Integer status);
    Result<List<Course>> getRecommendCourses();
}
