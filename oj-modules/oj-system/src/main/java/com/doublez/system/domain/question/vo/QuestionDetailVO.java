package com.doublez.system.domain.question.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionDetailVO {

    private Long questionId;

    private String title;

    private Integer difficulty;

    private Integer timeLimit;

    private Integer spaceLimit;

    private String content;

    private String questionCase;

    private String defaultCode;

    private String mainFuc;
}
