package com.doublez.system.service.user.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.system.domain.user.User;
import com.doublez.system.domain.user.dto.UserDTO;
import com.doublez.system.domain.user.dto.UserQueryDTO;
import com.doublez.system.domain.user.vo.UserVO;
import com.doublez.system.mapper.UserCacheManager;
import com.doublez.system.mapper.UserMapper;
import com.doublez.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCacheManager  userCacheManager;
    @Override
    public Page<UserVO> list(UserQueryDTO userQueryDTO) {
        Page<UserVO> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        return userMapper.selectUserList(page,userQueryDTO);
    }

    @Override
    public int updateStatus(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getUserId());
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setStatus(userDTO.getStatus());
        //拉黑后更新缓存
        userCacheManager.updateStatus(user.getUserId(), userDTO.getStatus());
        return userMapper.updateById(user);
    }
}
