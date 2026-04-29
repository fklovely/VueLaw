package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Answer;

public interface AnswerService extends IService<Answer> {
    PageResult<Answer> getAnswerList(Integer page, Integer size, Long questionId);
    Result<Boolean> addAnswer(Answer answer, Long userId, Integer userType);
    Result<Boolean> likeAnswer(Long id);
}
