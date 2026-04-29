package com.legal.platform.controller;

import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.Answer;
import com.legal.platform.entity.Question;
import com.legal.platform.service.AnswerService;
import com.legal.platform.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @GetMapping("/list")
    public Result<PageResult<Question>> getQuestionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer isSolved) {
        return Result.success(questionService.getQuestionList(page, size, keyword, categoryId, isSolved));
    }

    @GetMapping("/detail/{id}")
    public Result<Question> getQuestionDetail(@PathVariable Long id) {
        return questionService.getQuestionDetail(id);
    }

    @PostMapping
    public Result<Boolean> addQuestion(@RequestBody Question question, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return questionService.addQuestion(question, userId);
    }

    @PostMapping("/adopt")
    public Result<Boolean> adoptAnswer(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long questionId = params.get("questionId");
        Long answerId = params.get("answerId");
        Long userId = (Long) request.getAttribute("userId");
        return questionService.adoptAnswer(questionId, answerId, userId);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteQuestion(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return questionService.deleteQuestion(id, userId);
    }

    @GetMapping("/answer/list")
    public Result<PageResult<Answer>> getAnswerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long questionId) {
        return Result.success(answerService.getAnswerList(page, size, questionId));
    }

    @PostMapping("/answer")
    public Result<Boolean> addAnswer(@RequestBody Answer answer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        Integer userType = "lawyer".equals(role) ? 2 : 1;
        return answerService.addAnswer(answer, userId, userType);
    }

    @PostMapping("/answer/like/{id}")
    public Result<Boolean> likeAnswer(@PathVariable Long id) {
        return answerService.likeAnswer(id);
    }
}
