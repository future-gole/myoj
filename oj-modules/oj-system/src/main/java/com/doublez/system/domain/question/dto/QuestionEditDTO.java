package com.doublez.system.domain.question.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class QuestionEditDTO extends QuestionAddDTO{
    @NotNull
    private Long questionId;
}
