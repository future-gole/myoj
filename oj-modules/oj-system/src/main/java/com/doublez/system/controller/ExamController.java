package com.doublez.system.controller;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.exam.Exam;
import com.doublez.system.domain.exam.dto.ExamAddDTO;
import com.doublez.system.domain.exam.dto.ExamEditDTO;
import com.doublez.system.domain.exam.dto.ExamQueryDTO;
import com.doublez.system.domain.exam.dto.ExamQuestionAddDTO;
import com.doublez.system.domain.exam.vo.ExamDetailVO;
import com.doublez.system.service.exam.IExamService;
import com.doublez.system.service.exam.impl.ExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "管理员端的竞赛接口")
@RequestMapping("/exam")
public class ExamController extends BaseController {
    @Autowired
    private IExamService examService;

    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return getTableDataInfo(examService.list(examQueryDTO));
    }

    @PostMapping("/add")
    public R<Void> add(@RequestBody ExamAddDTO examQueryDTO) {
        return toR(examService.add(examQueryDTO));
    }

    @PostMapping("/question/add")
    public R<Void> questionAdd(@RequestBody ExamQuestionAddDTO examQuestionAddDTO) {
        return toR(examService.questionAdd(examQuestionAddDTO));
    }

    @DeleteMapping("/question/delete")
    public R<Void> questionDelete(Long examId, Long questionId) {
        return toR(examService.questionDelete(examId, questionId));
    }

    @GetMapping("/detail")
    public R<ExamDetailVO> detail(Long examId) {
        return R.ok(examService.getDetail(examId));
    }

    @PutMapping("/edit")
    public R<Void> edit(@RequestBody ExamEditDTO examEditDTO) {
        return toR(examService.edit(examEditDTO));
    }

    @DeleteMapping("/delete")
    public R<Void> delete(Long examId) {
        return toR(examService.delete(examId));
    }
}
