package com.doublez.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.system.domain.user.User;
import com.doublez.system.domain.user.dto.UserQueryDTO;
import com.doublez.system.domain.user.vo.UserVO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    Page<UserVO> selectUserList(IPage<UserVO> page, @Param("query") UserQueryDTO userQueryDTO);
}
