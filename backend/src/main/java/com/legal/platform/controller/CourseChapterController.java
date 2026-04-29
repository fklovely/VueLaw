package com.legal.platform.controller;

import com.legal.platform.common.Result;
import com.legal.platform.entity.CourseChapter;
import com.legal.platform.service.CourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/course-chapter")
public class CourseChapterController {

    @Autowired
    private CourseChapterService chapterService;

    @GetMapping("/list/{courseId}")
    public Result<List<CourseChapter>> getChapterList(@PathVariable Long courseId) {
        return chapterService.getChapterListByCourseId(courseId);
    }

    @PostMapping
    public Result<Boolean> addChapter(@RequestBody CourseChapter chapter, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return chapterService.addChapter(chapter, userId);
    }

    @PutMapping
    public Result<Boolean> updateChapter(@RequestBody CourseChapter chapter, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return chapterService.updateChapter(chapter, userId);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteChapter(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return chapterService.deleteChapter(id, userId);
    }
}
