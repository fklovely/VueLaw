package com.legal.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Course;
import com.legal.platform.entity.SysUser;
import com.legal.platform.mapper.CourseMapper;
import com.legal.platform.mapper.SysUserMapper;
import com.legal.platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public PageResult<Course> getCourseList(Integer page, Integer size, String keyword, Long categoryId, Integer isFree) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getTitle, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Course::getCategoryId, categoryId);
        }
        if (isFree != null) {
            wrapper.eq(Course::getIsFree, isFree);
        }
        wrapper.orderByAsc(Course::getId);
        IPage<Course> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public PageResult<Course> getMyCourseList(Integer page, Integer size, Long teacherId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        wrapper.orderByDesc(Course::getCreateTime);
        IPage<Course> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<Course> getCourseDetail(Long id) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        course.setViewCount(course.getViewCount() + 1);
        this.updateById(course);
        return Result.success(course);
    }

    @Override
    public Result<Boolean> addCourse(Course course, Long userId, String teacherName) {
        course.setCourseNo("COURSE" + IdUtil.getSnowflakeNextIdStr());
        course.setTeacherId(userId);
        course.setTeacherName(teacherName);
        course.setStatus(0);
        course.setViewCount(0);
        course.setStudentCount(0);
        course.setChapterCount(0);
        this.save(course);
        return Result.success("添加成功", true);
    }

    @Override
    public Result<Boolean> updateCourse(Course course, Long userId) {
        Course existCourse = this.getById(course.getId());
        if (existCourse == null) {
            return Result.error("课程不存在");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (!existCourse.getTeacherId().equals(userId) && !"admin".equals(user.getRole())) {
            return Result.error("无权操作");
        }
        existCourse.setTitle(course.getTitle());
        existCourse.setCategoryId(course.getCategoryId());
        existCourse.setCoverImage(course.getCoverImage());
        existCourse.setDescription(course.getDescription());
        existCourse.setCourseType(course.getCourseType());
        existCourse.setPrice(course.getPrice());
        existCourse.setDuration(course.getDuration());
        existCourse.setIsFree(course.getIsFree());
        existCourse.setTags(course.getTags());
        this.updateById(existCourse);
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Boolean> deleteCourse(Long id, Long userId) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (!course.getTeacherId().equals(userId) && !"admin".equals(user.getRole())) {
            return Result.error("无权操作");
        }
        this.removeById(id);
        return Result.success("删除成功", true);
    }

    @Override
    public Result<Boolean> publishCourse(Long id, Long userId) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        if (!course.getTeacherId().equals(userId)) {
            return Result.error("无权操作");
        }
        course.setStatus(0);
        course.setPublishTime(LocalDateTime.now());
        this.updateById(course);
        return Result.success("提交成功，请等待管理员审核", true);
    }

    @Override
    public Result<Boolean> auditCourse(Long id, Integer status) {
        Course course = this.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        course.setStatus(status);
        this.updateById(course);
        String msg = status == 1 ? "审核通过" : "审核拒绝";
        return Result.success(msg, true);
    }

    @Override
    public Result<List<Course>> getRecommendCourses() {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1).eq(Course::getIsRecommend, 1);
        wrapper.orderByDesc(Course::getRating);
        wrapper.last("LIMIT 10");
        List<Course> courses = this.list(wrapper);
        return Result.success(courses);
    }
}
