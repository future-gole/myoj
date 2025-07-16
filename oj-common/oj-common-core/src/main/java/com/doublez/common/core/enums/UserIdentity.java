package com.doublez.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserIdentity {

    ADMIN(1,"管理员"),
    NORMAL(2,"普通用户");

    private final Integer code;
    private final String des;
}
