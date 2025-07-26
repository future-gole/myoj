package com.doublez.friend.controller.user;

import com.doublez.api.domain.domain.vo.UserQuestionResultVO;
import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.friend.domain.user.dto.UserSubmitDTO;
import com.doublez.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/question")
public class UserQuestionController extends BaseController {

    @Autowired
    private IUserQuestionService userQuestionService;

    //用户代码提交   请求方法  地址  参数  响应数据结构
    @PostMapping("/submit")
    public R<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO submitDTO) {
        return userQuestionService.submit(submitDTO);
    }
}