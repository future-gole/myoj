package com.doublez.system.controller;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.exam.Exam;
import com.doublez.system.domain.exam.dto.ExamQueryDTO;
import com.doublez.system.service.exam.IExamService;
import com.doublez.system.service.exam.impl.ExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "管理员端的竞赛接口")
@RequestMapping("/exam")
public class ExamController extends BaseController {
    @Autowired
    private IExamService examService;

    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        TableDataInfo tableDataInfo = getTableDataInfo(examService.list(examQueryDTO));
        return tableDataInfo;
    }
}
