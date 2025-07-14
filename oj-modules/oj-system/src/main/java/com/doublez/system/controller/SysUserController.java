package com.doublez.system.controller;

import com.doublez.common.core.domain.R;
import com.doublez.system.domain.LoginDTO;
import com.doublez.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired
    private ISysUserService sysUserService;

    @PostMapping("/login")
    public R<Void> login(@RequestBody LoginDTO loginDTO) {
        return sysUserService.login(loginDTO.getUsername(), loginDTO.getPassword());
    }
}
