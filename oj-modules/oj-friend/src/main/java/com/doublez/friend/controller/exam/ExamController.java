package com.doublez.friend.controller.exam;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.exam.dto.ExamQueryDTO;
import com.doublez.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private IExamService examService;

    @GetMapping("/semiLogin/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return getTableDataInfo(examService.list(examQueryDTO));
    }

    @GetMapping("/semiLogin/redis/list")
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        return examService.redisList(examQueryDTO);
    }

    @GetMapping("/getFirstQuestion")
    public R<String> getFirstQuestion(Long examId) {
        // 排在第一个的题目  返回给前端
        return R.ok(examService.getFirstQuestion(examId));
    }

}
