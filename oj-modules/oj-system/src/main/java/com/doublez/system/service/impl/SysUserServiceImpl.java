package com.doublez.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.system.controller.LoginResult;
import com.doublez.system.domain.SysUser;
import com.doublez.system.mapper.SysUserMapper;
import com.doublez.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public R<Void> login(String username, String password) {
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().select(SysUser::getUserAccount));
        R<Void> loginResult = new R<>();
        if(sysUser==null){
            loginResult.setCode(ResultCode.FAILED_USER_NOT_EXISTS.getCode());
            loginResult.setMsg(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
            return loginResult;
        }
        if(!sysUser.getPassword().equals(password)){
            loginResult.setCode(ResultCode.FAILED_LOGIN.getCode());
            loginResult.setMsg(ResultCode.FAILED_LOGIN.getMsg());
            return loginResult;
        }
        loginResult.setMsg(ResultCode.SUCCESS.getMsg());
        loginResult.setCode(ResultCode.SUCCESS.getCode());
        return loginResult;
    }
}
