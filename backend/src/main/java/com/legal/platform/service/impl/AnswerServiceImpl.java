package com.legal.platform.service.impl;

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
import com.legal.platform.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public PageResult<Answer> getAnswerList(Integer page, Integer size, Long questionId) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getQuestionId, questionId);
        wrapper.eq(Answer::getStatus, 1);
        wrapper.orderByDesc(Answer::getIsBest);
        wrapper.orderByDesc(Answer::getLikeCount);
        IPage<Answer> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<Boolean> addAnswer(Answer answer, Long userId, Integer userType) {
        Question question = questionMapper.selectById(answer.getQuestionId());
        if (question == null) {
            return Result.error("问题不存在");
        }
        answer.setUserId(userId);
        answer.setUserType(userType);
        answer.setStatus(1);
        answer.setLikeCount(0);
        answer.setIsBest(0);
        answer.setIsAdopted(0);
        this.save(answer);
        question.setAnswerCount(question.getAnswerCount() + 1);
        questionMapper.updateById(question);
        return Result.success("回答成功", true);
    }

    @Override
    public Result<Boolean> likeAnswer(Long id) {
        Answer answer = this.getById(id);
        if (answer == null) {
            return Result.error("回答不存在");
        }
        answer.setLikeCount(answer.getLikeCount() + 1);
        this.updateById(answer);
        return Result.success(true);
    }
}
