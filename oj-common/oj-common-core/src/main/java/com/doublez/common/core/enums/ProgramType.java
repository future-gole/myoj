package com.doublez.common.core.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ProgramType {

    JAVA(0, "java"),
    CPP(1, "C++"),
    GOLANG(2, "go");

    private final Integer value;
    private final String desc;

    ProgramType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据 value 获取枚举对象（使用Stream API）
     * @param value 传入的整数值
     * @return 匹配的枚举对象，如果找不到则返回 null
     */
    public static ProgramType of(Integer value) {
        if (value == null) {
            return null;
        }
        return Stream.of(values()) // 将所有枚举实例转换为Stream
                .filter(type -> value.equals(type.getValue())) // 过滤出value匹配的项
                .findFirst() // 获取第一个匹配项
                .orElse(null); // 如果找不到，则返回null
    }
}
