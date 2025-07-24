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

    FAILED_USER_EMAIL(3205,"邮箱已经被使用"),
    FAILED_SEND_CODE(3206,"邮箱验证码发送失败"),
    FAILED_INVALID_CODE(3107,"验证码无效"),
    FAILED_ERROR_CODE(3108,"验证码错误"),
    FAILED_TIME_LIMIT(3109,"验证码次数超出当日上限"),

    EXAM_START_TIME_BEFORE_CURRENT_TIME(3202,"开始时间不能在当前时间之前"),
    EXAM_START_TIME_AFTER_END_TIME(3202,"开始时间不能晚于结束时间"),

    EXAM_NOT_EXITS(3203,"竞赛不存在"),
    EXAM_QUESTION_NOT_EXITS(3204,"竞赛所需添加的题目不存在"),
    EXAM_IS_PUBLISH(3205,"竞赛已经发布"),
    EXAM_STARTED(3206,"竞赛已经开始"),
    EXAM_IS_FINISH(3207,"竞赛已经结束不能操作" ),
    EXAM_NOT_HAS_QUESTION(3208,"竞赛没有题目" ),

    USER_EXAM_HAS_ENTER(3301, "用户已经报过名，无需重复报名"),

    FAILED_FILE_UPLOAD                  (3401, "文件上传失败"),

    FAILED_FILE_UPLOAD_TIME_LIMIT       (3402, "当天上传图片数量超过上限");

    private final int code;
    private final String msg;
}
