package com.doublez.friend.controller;

import com.doublez.common.core.constants.HttpConstants;
import com.doublez.common.core.controller.BaseController;
import com.doublez.common.core.domain.R;
import com.doublez.friend.domain.dto.UserDTO;
import com.doublez.friend.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户端接口")
@RequestMapping("/user")
@RestController
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO){
        return toR(userService.sendCode(userDTO.getEmail()));
    }

//    @PostMapping("/code/login")
//    public R<String> codeLogin(@RequestBody UserDTO userDTO) {
//        return R.ok(userService.codeLogin(userDTO.getPhone(), userDTO.getCode()));
//    }

    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return toR(userService.logout(token));
    }
}
