package com.doublez.friend.service.user;

import com.doublez.common.core.domain.R;
import com.doublez.friend.domain.user.dto.UserDTO;
import com.doublez.friend.domain.user.vo.LoginUserVO;
import jakarta.validation.constraints.Email;

public interface IUserService {
    boolean logout(String token);

//    String codeLogin(String phone, String code);

    boolean sendCode(@Email String email);

    String codeLogin(UserDTO user);

    R<LoginUserVO> info(String token);
}
