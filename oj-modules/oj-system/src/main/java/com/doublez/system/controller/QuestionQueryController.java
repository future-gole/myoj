package com.doublez.system.controller;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.question.dto.QuestionAddDTO;
import com.doublez.system.domain.question.dto.QuestionEditDTO;
import com.doublez.system.domain.question.dto.QuestionQueryDTO;
import com.doublez.system.domain.question.vo.QuestionDetailVO;
import com.doublez.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "添加题目")
    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody QuestionAddDTO addDTO){
        return toR(questionService.add(addDTO));
    }

    @Operation(summary = "获取题目详情")
    @GetMapping("/detail")
    public R<QuestionDetailVO> getDetail(@NotNull Long questionId){
        return R.ok(questionService.getDetail(questionId));
    }

    @Operation(summary = "修改题目详情")
    @PostMapping("/edit")
    public R<Void> edit(@Validated @RequestBody QuestionEditDTO editDTO){
        return toR(questionService.edit(editDTO));
    }

    @Operation(summary = "删除题目")
    @DeleteMapping("/delete")
    public R<Void> delete(@NotNull Long questionId){
        return toR(questionService.delete(questionId));
    }
}
