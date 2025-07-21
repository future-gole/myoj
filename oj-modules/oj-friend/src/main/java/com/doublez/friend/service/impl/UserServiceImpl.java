package com.doublez.friend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doublez.common.core.constants.CacheConstants;
import com.doublez.common.core.constants.HttpConstants;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.core.enums.UserIdentity;
import com.doublez.common.message.service.EmailService;
import com.doublez.common.redis.service.RedisService;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.friend.domain.User;
import com.doublez.friend.mapper.UserMapper;
import com.doublez.friend.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.doublez.common.security.service.TokenService;

@Slf4j
@Service
public class UserServiceImpl  implements IUserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private RedisService redisService;

    @Override
    public boolean sendCode(String email) {
        //生成6位随机数
        String code = RandomUtil.randomString(6);
        //发送验证码
        try{
            emailService.sendSimpleMail(email,code);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ServiceException(ResultCode.EMAIL_CODE_FAIL_SEND);
        }
        //存储到redis
        redisService.setCacheObject(getEmailCodeKey(email), code);
        return true;
    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token, secret);
    }

//    @Override
//    public String codeLogin(String email, String code) {
//        checkCode(email, code);
//        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, email));
//        if (user == null) {  //新用户
//            //注册逻辑
//            user = new User();
//            user.setEmail(email);
//            user.setStatus(UserStatus.Normal.getValue());
//            user.setCreateBy(Constants.SYSTEM_USER_ID);
//            userMapper.insert(user);
//        }
//        return tokenService.createToken(user.getUserId(), secret, UserIdentity.ORDINARY.getCode(), user.getNickName(), user.getHeadImage());
//    }

    private String getEmailCodeKey(String email) {
        return CacheConstants.EMAIL_CODE_KEY + email;
    }


}
