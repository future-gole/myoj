package com.doublez.friend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.doublez.common.core.constants.HttpConstants;
import com.doublez.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.doublez.common.security.service.TokenService;

@Service
public class UserServiceImpl  implements IUserService {

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token, secret);
    }
}
