package com.doublez.judge.service;

import com.doublez.api.domain.domain.dto.JudgeSubmitDTO;
import com.doublez.api.domain.domain.vo.UserQuestionResultVO;

public interface IJudgeService {
    UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}
