package com.doublez.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatus {

    BLOCK(0),
    NORMAL(1);


    private Integer value;
}
