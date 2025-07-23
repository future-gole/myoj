package com.doublez.friend.domain.question;

import com.doublez.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionQueryDTO extends PageQueryDTO {

    private String keyword;

    private Integer difficulty;
}
