package com.doublez.system.service.sysuser;

import com.doublez.common.core.domain.R;
import com.doublez.system.domain.sysuser.vo.LoginUserVO;
import com.doublez.system.domain.sysuser.dto.SysUserSaveDTO;
import jakarta.validation.constraints.NotBlank;

public interface ISysUserService {
    R<String> login(String username, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);

    R<LoginUserVO> info(@NotBlank String token);

    boolean logout(String token);
}
