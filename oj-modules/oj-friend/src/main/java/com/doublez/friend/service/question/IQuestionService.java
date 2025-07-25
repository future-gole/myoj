package com.doublez.friend.service.question;

import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.question.QuestionQueryDTO;
import com.doublez.friend.domain.question.vo.QuestionDetailVO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    QuestionDetailVO detail(Long questionId);

    String preQuestion(Long questionId);

    String nextQuestion(Long questionId);
}
