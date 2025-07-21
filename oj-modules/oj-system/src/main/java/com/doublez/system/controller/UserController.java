package com.doublez.system.controller;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.user.dto.UserDTO;
import com.doublez.system.domain.user.dto.UserQueryDTO;
import com.doublez.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;


    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO) {
        return getTableDataInfo(userService.list(userQueryDTO));
    }

    @PutMapping("/updateStatus")
    //todo 拉黑：限制用户操作   解禁：放开对于用户限制
    //更新数据库中用户的状态信息。
    public R<Void> updateStatus(@RequestBody UserDTO userDTO) {
        return toR(userService.updateStatus(userDTO));
    }
}