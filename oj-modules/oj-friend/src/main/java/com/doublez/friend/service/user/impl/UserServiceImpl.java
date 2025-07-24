package com.doublez.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doublez.common.core.constants.CacheConstants;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.constants.HttpConstants;
import com.doublez.common.core.domain.LoginUser;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.core.enums.UserIdentity;
import com.doublez.common.core.enums.UserStatus;
import com.doublez.common.core.utils.ThreadLocalUtil;
import com.doublez.common.message.service.EmailService;
import com.doublez.common.redis.service.RedisService;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.common.security.service.TokenService;
import com.doublez.friend.domain.user.User;
import com.doublez.friend.domain.user.dto.UserDTO;
import com.doublez.friend.domain.user.dto.UserUpdateDTO;
import com.doublez.friend.domain.user.vo.LoginUserVO;
import com.doublez.friend.domain.user.vo.UserVO;
import com.doublez.friend.manager.UserCacheManager;
import com.doublez.friend.mapper.user.UserMapper;
import com.doublez.friend.service.user.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl  implements IUserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private RedisService redisService;

    @Value("${email.send-limit}")
    private int sendLimit;

    @Value("${email.is-send:ture}")
    private boolean isSend;

    @Value("${email.emailCodeExpiration}")
    private Long emailCodeExpiration;

    @Override
    public boolean sendCode(String email) {
        //邮箱获取有限制
        String codeTimeKey = getCodeTimeKey(email);
        Long sendTimes = redisService.getCacheObject(codeTimeKey, Long.class);
        if (sendTimes != null && sendTimes >= sendLimit) {
            throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
        }
        String code = isSend ? RandomUtil.randomNumbers(6) : Constants.DEFAULT_CODE;
        //存储到redis  数据结构：String  key：e:c:邮箱  value :code
        if (isSend) {
            //发送验证码
            try{
                emailService.sendSimpleMail(email,code);
            }catch (Exception e){
                log.error(e.getMessage());
                throw new ServiceException(ResultCode.FAILED_SEND_CODE);
            }
        }
        redisService.increment(codeTimeKey);
        if (sendTimes == null) {  //说明是当天第一次发起获取验证码的请求
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            redisService.expire(codeTimeKey, seconds, TimeUnit.SECONDS);
        }
        //存储到redis
        redisService.setCacheObject(getEmailCodeKey(email), code,emailCodeExpiration, TimeUnit.MINUTES);
        return true;
    }


    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token, secret);
    }

    @Override
    public String codeLogin(UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();
        checkCode(email, code);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (user == null) {  //新用户
            //注册逻辑
            user = new User();
            user.setEmail(email);
            user.setStatus(UserStatus.NORMAL.getValue());
//            user.setCreateBy(Constants.SYSTEM_USER_ID);
            userMapper.insert(user);
        }
        return tokenService.createToken(user.getUserId(), secret, UserIdentity.ORDINARY.getCode(), user.getNickName(),user.getHeadImage());
    }

    @Override
    public R<LoginUserVO> info(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        LoginUser loginUser = tokenService.getLoginUser(token, secret);
        if (loginUser == null) {
            return R.fail();
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickname(loginUser.getNickname());
        loginUserVO.setHeadImage(loginUser.getHeadImage());
//        if (StrUtil.isNotEmpty(loginUser.getHeadImage())) {
//            loginUserVO.setHeadImage(downloadUrl + loginUser.getHeadImage());
//        }
        return R.ok(loginUserVO);
    }

    @Override
    public UserVO detail() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
//        if (StrUtil.isNotEmpty(userVO.getHeadImage())) {
//            userVO.setHeadImage(downloadUrl + userVO.getHeadImage());
//        }
        return userVO;
    }

    @Override
    public int edit(UserUpdateDTO userUpdateDTO) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        BeanUtil.copyProperties(userUpdateDTO, user);
        //更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        return userMapper.updateById(user);
    }

    @Override
    public int updateHeadImage(String headImage) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setHeadImage(headImage);
        //更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        return userMapper.updateById(user);
    }

    private void checkCode(String email, String code) {
        if(StrUtil.isEmpty(code)){
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        String emailCodeKey = getEmailCodeKey(email);
        String trueCode = redisService.getCacheObject(emailCodeKey, String.class);
        if(StrUtil.isEmpty(trueCode)) {
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        if(!code.equals(trueCode)) {
            throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
        }
        //删除redis中的code
        redisService.deleteObject(emailCodeKey);
    }

    private boolean checkEmail(String email) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        return user == null;
    }

    private String getEmailCodeKey(String email) {
        return CacheConstants.EMAIL_CODE_KEY + email;
    }

    private String getCodeTimeKey(String email) {
        return CacheConstants.EMAIL_CODE_KEY_LIMIT + email;
    }

}
