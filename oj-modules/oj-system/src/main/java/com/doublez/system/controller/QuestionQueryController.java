package com.doublez.system.controller;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.question.dto.QuestionQueryDTO;
import com.doublez.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "管理员端的题目接口")
@RequestMapping("/question")
public class QuestionQueryController extends BaseController {
    @Autowired
    private IQuestionService questionService;

    @Operation(summary = "获取题目列表")
    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO queryDTO){
        return getTableDataInfo(questionService.list(queryDTO));
    }
}
