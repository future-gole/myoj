package com.doublez.system.service;

import com.doublez.common.core.domain.R;
import com.doublez.system.controller.LoginResult;

public interface ISysUserService {
    R<String> login(String username, String password);
}
