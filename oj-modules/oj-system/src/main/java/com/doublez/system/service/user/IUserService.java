package com.doublez.system.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.system.domain.user.dto.UserQueryDTO;

public interface IUserService {
    IPage<?> list(UserQueryDTO userQueryDTO);
}
