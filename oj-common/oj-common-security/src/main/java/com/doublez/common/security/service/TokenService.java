package com.doublez.common.security.service;

import com.doublez.common.core.constants.CacheConstants;
import com.doublez.common.core.constants.JwtConstants;
import com.doublez.common.core.domain.LoginUser;
import com.doublez.common.core.utils.JwtUtils;
import com.doublez.common.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Service
@Slf4j
public class TokenService {

    @Autowired
    private RedisService redisService;

    public String createToken(Long userId,String secret,Integer identity,String nickName,String headImage) {
        Map<String,Object> claims = new HashMap<>();
        String userKey = UUID.randomUUID().toString();
        claims.put(JwtConstants.Login_User_ID,userId);
        claims.put(JwtConstants.Login_User_Key,userKey);
        String token = JwtUtils.createToken(claims, secret);
        //3.2 将用户信息存储到redis中
        String key = getRedisUserKey(userKey);
        //1 表示c端用户 2 表示管理员用户
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity);
        loginUser.setNickname(nickName);
        loginUser.setHeadImage(headImage);
        redisService.setCacheObject(key,loginUser, CacheConstants.Login_Token_expire, TimeUnit.MINUTES);

        return token;
    }

    public void extendToken(String token, String secret) {
        String userKey = getUserKey(token,secret);
        if(userKey == null) {
            return;
        }
        String Key = getRedisUserKey(userKey);
        //redis查询
        Long expire = redisService.getExpire(Key, TimeUnit.MINUTES);
        if(expire != null && expire < CacheConstants.LOGIN_TOKEN_REFRESH){
            redisService.expire(Key, CacheConstants.Login_Token_expire, TimeUnit.MINUTES);
        }
    }
    public LoginUser getLoginUser(String token,String secret) {
        String userKey = getUserKey(token, secret);
        if(userKey == null) {
            return null;
        }
        String key = getRedisUserKey(userKey);
        return redisService.getCacheObject(key, LoginUser.class);
    }

    public boolean deleteLoginUser(String token,String secret) {
        String userKey = getUserKey(token, secret);
        if(userKey == null) {
            return false;
        }
        String key = getRedisUserKey(userKey);
        return redisService.deleteObject(key);
    }

    private static String getRedisUserKey(String userKey) {
        return CacheConstants.Login_Token_Key + userKey;
    }

    public String getUserKey(String token, String secret) {
        Claims claims = getClaims(token, secret);
        if (claims == null) return null;
        //获取key
        return JwtUtils.getUserKey(claims);
    }

    public Long getUserId(String token, String secret) {
        Claims claims = getClaims(token, secret);
        if (claims == null) return null;
        return Long.valueOf(JwtUtils.getUserId(claims));
    }

    public void refreshLoginUser(String nickName, String headImage, String userKey) {
        String tokenKey = getTokenKey(userKey);
        LoginUser loginUser = redisService.getCacheObject(tokenKey, LoginUser.class);
        loginUser.setNickname(nickName);
        loginUser.setHeadImage(headImage);
        redisService.setCacheObject(tokenKey, loginUser);
    }

    private String getTokenKey(String userKey) {
        return CacheConstants.Login_Token_Key + userKey;
    }

    private static Claims getClaims(String token, String secret) {
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);
            //正常来说不会为空，以防万一
            if (claims == null) {
                log.error("解析token：{}, 出现异常", token);
                return null;
            }
            //正常来说不会抛异常，以防万一
        } catch (Exception e) {
            log.error("解析token：{}, 出现异常", token);
            return null;
        }
        return claims;
    }
}
