package com.doublez.common.security.service;

import com.doublez.common.core.constants.CacheConstants;
import com.doublez.common.core.constants.JwtConstants;
import com.doublez.common.core.domain.LoginUser;
import com.doublez.common.redis.service.RedisService;
import com.doublez.common.security.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Service
public class TokenService {

    @Autowired
    private RedisService redisService;

    public String createToken(Long userId,String secret,Integer identity) {
        Map<String,Object> claims = new HashMap<>();
        String userKey = UUID.randomUUID().toString();
        claims.put(JwtConstants.Login_User_ID,userId);
        claims.put(JwtConstants.Login_User_Key,userKey);
        String token = JwtUtils.createToken(claims, secret);
        //3.2 将用户信息存储到redis中
        String key = CacheConstants.Login_Token_Key + userKey;
        //1 表示c端用户 2 表示管理员用户
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity);
        redisService.setCacheObject(key,loginUser, CacheConstants.Login_Token_expire, TimeUnit.MINUTES);

        return token;
    }
}
