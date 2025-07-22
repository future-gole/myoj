package com.doublez.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doublez.friend.domain.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
