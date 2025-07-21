package com.doublez.system.controller;

import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.user.dto.UserQueryDTO;
import com.doublez.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;


    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO) {
        return getTableDataInfo(userService.list(userQueryDTO));
    }
}