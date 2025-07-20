package com.doublez.system.domain.question.dto;

import com.doublez.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class QuestionQueryDTO extends PageQueryDTO {

    private Integer difficulty;

    private String title;

    private String excludeIdStr;//  ;

    private Set<Long> excludeIdSet;
}
