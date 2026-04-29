package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Question;

public interface QuestionService extends IService<Question> {
    PageResult<Question> getQuestionList(Integer page, Integer size, String keyword, Long categoryId, Integer isSolved);
    Result<Question> getQuestionDetail(Long id);
    Result<Boolean> addQuestion(Question question, Long userId);
    Result<Boolean> adoptAnswer(Long questionId, Long answerId, Long userId);
    Result<Boolean> deleteQuestion(Long id, Long userId);
}
