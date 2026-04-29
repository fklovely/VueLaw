package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Course;
import com.legal.platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public Result<PageResult<Course>> getCourseList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer isFree) {
        return Result.success(courseService.getCourseList(page, size, keyword, categoryId, isFree));
    }

    @GetMapping("/my-list")
    public Result<PageResult<Course>> getMyCourseList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long teacherId = (Long) request.getAttribute("userId");
        return Result.success(courseService.getMyCourseList(page, size, teacherId));
    }

    @GetMapping("/detail/{id}")
    public Result<Course> getCourseDetail(@PathVariable Long id) {
        return courseService.getCourseDetail(id);
    }

    @PostMapping
    public Result<Boolean> addCourse(@RequestBody Course course, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String teacherName = (String) request.getAttribute("realName");
        return courseService.addCourse(course, userId, teacherName);
    }

    @PutMapping
    public Result<Boolean> updateCourse(@RequestBody Course course, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return courseService.updateCourse(course, userId);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCourse(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return courseService.deleteCourse(id, userId);
    }

    @PostMapping("/publish/{id}")
    public Result<Boolean> publishCourse(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return courseService.publishCourse(id, userId);
    }

    @PutMapping("/audit/{id}")
    public Result<Boolean> auditCourse(@PathVariable Long id, @RequestParam Integer status) {
        return courseService.auditCourse(id, status);
    }

    @GetMapping("/recommend")
    public Result<List<Course>> getRecommendCourses() {
        return courseService.getRecommendCourses();
    }
}
