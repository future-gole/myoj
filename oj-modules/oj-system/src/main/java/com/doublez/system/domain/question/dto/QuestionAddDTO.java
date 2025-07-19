package com.doublez.system.domain.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class QuestionAddDTO {
    @NotBlank
    private String title;
    @NotNull
    private Integer difficulty;
    @NotNull
    private Integer timeLimit;
    @NotNull
    private Integer spaceLimit;
    @NotBlank
    private String content;
    @NotBlank
    private String questionCase;
    @NotBlank
    private String defaultCode;
    @NotBlank
    private String mainFuc;
}
