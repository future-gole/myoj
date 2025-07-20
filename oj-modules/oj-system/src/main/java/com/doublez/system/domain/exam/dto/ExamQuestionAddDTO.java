package com.doublez.system.domain.exam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
@Getter
@Setter
public class ExamQuestionAddDTO {
    @NotNull
    private Long examId;
    @NotNull
    private LinkedHashSet<Long> questionIds;
}
