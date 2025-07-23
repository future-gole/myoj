package com.doublez.friend.service.question;

import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.question.QuestionQueryDTO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);
}
