package com.doublez.friend.controller.user;

import com.doublez.api.domain.vo.UserQuestionResultVO;
import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.friend.aspect.CheckUserStatus;
import com.doublez.friend.domain.user.dto.UserSubmitDTO;
import com.doublez.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/question")
public class UserQuestionController extends BaseController {

    @Autowired
    private IUserQuestionService userQuestionService;

    //用户代码提交   请求方法  地址  参数  响应数据结构
    @CheckUserStatus
    @PostMapping("/submit")
    public R<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO submitDTO) {
        return userQuestionService.submit(submitDTO);
    }
    @CheckUserStatus
    @PostMapping("/rabbit/submit")
    public R<Void> rabbitSubmit(@RequestBody UserSubmitDTO submitDTO) {
        return toR(userQuestionService.rabbitSubmit(submitDTO));
    }
    @CheckUserStatus
    @GetMapping("/exe/result")
    public  R<UserQuestionResultVO> exeResult(Long examId, Long questionId, String currentTime) {
        return R.ok(userQuestionService.exeResult(examId, questionId, currentTime));
    }
}