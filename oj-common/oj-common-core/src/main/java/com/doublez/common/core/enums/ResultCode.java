package com.doublez.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    //操作唱功
    SUCCESS (1000, "操作成功"),
    //服务器内部错误，友好提⽰
    ERROR (2000, "服务繁忙请稍后重试"),
    //操作失败，但是服务器不存在异常
    FAILED (3000, "操作失败"),
    FAILED_UNAUTHORIZED (3001, "未授权"),
    FAILED_PARAMS_VALIDATE (3002, "参数校验失败"),
    FAILED_NOT_EXISTS (3003, "资源不存在"),
    FAILED_ALREADY_EXISTS (3004, "资源已存在"),
    AILED_USER_EXISTS (3101, "⽤⼾已存在"),
    FAILED_USER_NOT_EXISTS (3102, "⽤⼾不存在"),
    FAILED_LOGIN (3103, "⽤⼾名或密码错误"),
    FAILED_USER_BANNED (3104, "您已被列⼊⿊名单, 请联系管理员."),

    EXAM_START_TIME_BEFORE_CURRENT_TIME(3202,"开始时间不能在当前时间之前"),
    EXAM_START_TIME_AFTER_END_TIME(3202,"开始时间不能晚于结束时间");
    private final int code;
    private final String msg;
}
