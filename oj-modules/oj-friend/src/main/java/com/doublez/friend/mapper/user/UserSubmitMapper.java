package com.doublez.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doublez.friend.domain.user.UserSubmit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSubmitMapper extends BaseMapper<UserSubmit> {

    UserSubmit selectCurrentUserSubmit(Long userId, Long examId, Long questionId, String currentTime);
}
