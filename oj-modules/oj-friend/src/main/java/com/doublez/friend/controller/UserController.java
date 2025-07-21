package com.doublez.friend.controller;

import com.doublez.common.core.constants.HttpConstants;
import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.friend.domain.dto.UserDTO;
import com.doublez.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO){
        return null;
    }

    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return toR(userService.logout(token));
    }
}
