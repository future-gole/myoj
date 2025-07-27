package com.doublez.friend.controller.user;

import com.doublez.common.core.constants.HttpConstants;
import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.aspect.CheckUserStatus;
import com.doublez.friend.domain.exam.dto.ExamDTO;
import com.doublez.friend.domain.exam.dto.ExamQueryDTO;
import com.doublez.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/exam")
public class UserExamController extends BaseController {

    @Autowired
    private IUserExamService userExamService;


    @CheckUserStatus
    @PostMapping("/enter")
    public R<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ExamDTO examDTO) {
        return toR(userExamService.enter(token, examDTO.getExamId()));
    }

    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return userExamService.list(examQueryDTO);
    }
}
