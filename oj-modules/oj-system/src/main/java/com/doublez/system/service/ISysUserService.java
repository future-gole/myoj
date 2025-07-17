package com.doublez.system.service;

import com.doublez.common.core.domain.R;
import com.doublez.system.controller.LoginResult;
import com.doublez.system.domain.SysUserSaveDTO;

public interface ISysUserService {
    R<String> login(String username, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);
}
