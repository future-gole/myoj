package com.doublez.common.core.enums;

import lombok.Getter;

@Getter
public enum ProgramType {

    JAVA(0, "java"),

    CPP(1, "C++"),

    GOLANG(2, "go");

    private Integer value;

    private String desc;

    ProgramType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
