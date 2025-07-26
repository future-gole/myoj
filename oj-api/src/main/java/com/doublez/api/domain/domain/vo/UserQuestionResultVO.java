package com.doublez.api.domain.domain.vo;

import com.doublez.api.domain.domain.UserExeResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserQuestionResultVO {
    //是够通过标识
    private Integer pass; // 0  未通过  1 通过

    private String exeMessage; //异常信息

    private List<UserExeResult> userExeResultList;

    private Integer score;
}
