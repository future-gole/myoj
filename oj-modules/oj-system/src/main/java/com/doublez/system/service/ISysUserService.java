package com.doublez.system.service;

import com.doublez.common.core.domain.R;
import com.doublez.system.domain.DTO.SysUserSaveDTO;

public interface ISysUserService {
    R<String> login(String username, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);
}
