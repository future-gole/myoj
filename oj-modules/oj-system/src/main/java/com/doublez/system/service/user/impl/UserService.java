package com.doublez.system.service.user.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.system.domain.user.dto.UserQueryDTO;
import com.doublez.system.domain.user.vo.UserVO;
import com.doublez.system.mapper.UserMapper;
import com.doublez.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public Page<UserVO> list(UserQueryDTO userQueryDTO) {
        Page<UserVO> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        return userMapper.selectUserList(page,userQueryDTO);
    }
}
