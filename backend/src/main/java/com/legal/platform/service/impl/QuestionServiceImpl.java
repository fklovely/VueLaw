package com.legal.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Answer;
import com.legal.platform.entity.Question;
import com.legal.platform.mapper.AnswerMapper;
import com.legal.platform.mapper.QuestionMapper;
import com.legal.platform.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public PageResult<Question> getQuestionList(Integer page, Integer size, String keyword, Long categoryId, Integer isSolved) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Question::getTitle, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Question::getCategoryId, categoryId);
        }
        if (isSolved != null) {
            wrapper.eq(Question::getIsSolved, isSolved);
        }
        wrapper.orderByDesc(Question::getCreateTime);
        IPage<Question> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<Question> getQuestionDetail(Long id) {
        Question question = this.getById(id);
        if (question == null) {
            return Result.error("问题不存在");
        }
        question.setViewCount(question.getViewCount() + 1);
        this.updateById(question);
        return Result.success(question);
    }

    @Override
    public Result<Boolean> addQuestion(Question question, Long userId) {
        question.setQuestionNo("Q" + IdUtil.getSnowflakeNextIdStr());
        question.setUserId(userId);
        question.setStatus(1);
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setLikeCount(0);
        question.setIsSolved(0);
        this.save(question);
        return Result.success("提问成功", true);
    }

    @Override
    public Result<Boolean> adoptAnswer(Long questionId, Long answerId, Long userId) {
        Question question = this.getById(questionId);
        if (question == null) {
            return Result.error("问题不存在");
        }
        if (!question.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            return Result.error("回答不存在");
        }
        answer.setIsBest(1);
        answer.setIsAdopted(1);
        answerMapper.updateById(answer);
        question.setIsSolved(1);
        question.setBestAnswerId(answerId);
        this.updateById(question);
        return Result.success("采纳成功", true);
    }

    @Override
    public Result<Boolean> deleteQuestion(Long id, Long userId) {
        Question question = this.getById(id);
        if (question == null) {
            return Result.error("问题不存在");
        }
        if (!question.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        this.removeById(id);
        return Result.success("删除成功", true);
    }
}
