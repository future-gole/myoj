package com.doublez.friend.controller.question;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.question.QuestionQueryDTO;
import com.doublez.friend.domain.question.vo.QuestionDetailVO;
import com.doublez.friend.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;

    @GetMapping("/semiLogin/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        return questionService.list(questionQueryDTO);
    }

    @GetMapping("/detail")
    public R<QuestionDetailVO> detail(Long questionId) {
        return R.ok(questionService.detail(questionId));
    }

    //获取题目的顺序
    @GetMapping("/preQuestion")
    public R<String> preQuestion(Long questionId) {
        return R.ok(questionService.preQuestion(questionId));
    }

    @GetMapping("/nextQuestion")
    public R<String> nextQuestion(Long questionId) {
        return R.ok(questionService.nextQuestion(questionId));
    }

}