package com.doublez.friend.service.user;

import com.doublez.api.domain.vo.UserQuestionResultVO;
import com.doublez.common.core.domain.R;
import com.doublez.friend.domain.user.dto.UserSubmitDTO;

public interface IUserQuestionService {
    R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO);

    boolean rabbitSubmit(UserSubmitDTO submitDTO);

    UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime);
}