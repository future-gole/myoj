package com.doublez.system.service;

import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.LoginUserVO;
import com.doublez.system.domain.DTO.SysUserSaveDTO;
import jakarta.validation.constraints.NotBlank;

public interface ISysUserService {
    R<String> login(String username, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);

    R<LoginUserVO> info(@NotBlank String token);
}
