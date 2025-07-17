package com.doublez.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.core.enums.UserIdentity;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.common.security.service.TokenService;
import com.doublez.system.domain.SysUser;
import com.doublez.system.domain.SysUserSaveDTO;
import com.doublez.system.mapper.SysUserMapper;
import com.doublez.system.service.ISysUserService;
import com.doublez.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private TokenService tokenService;

    @Override
    public R<String> login(String userAccount, String password) {
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getPassword,SysUser::getUserId)
                .eq(SysUser::getUserAccount,userAccount));
        //1. 用户不存在
        if(sysUser==null){
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        //2. 密码错误
        if(!BCryptUtils.matchesPassword(password,sysUser.getPassword())){
            return R.fail(ResultCode.FAILED_LOGIN);
        }
        //3. 正确
        //3.1 创建token

        return R.ok(tokenService.createToken(sysUser.getUserId(),secret, UserIdentity.ADMIN.getCode()));
    }

    @Override
    public int add(SysUserSaveDTO sysUserSaveDTO) {
        //判断account是否存在
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));
        if(count > 0){
            throw new ServiceException(ResultCode.AILED_USER_EXISTS);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword()));
        return sysUserMapper.insert(sysUser);
    }
}
