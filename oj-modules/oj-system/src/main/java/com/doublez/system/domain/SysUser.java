package com.doublez.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.doublez.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;


@TableName("tb_sys_user")
@Getter
@Setter
public class SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;//使用雪花算法
    private String userAccount;
    private String password;
}
