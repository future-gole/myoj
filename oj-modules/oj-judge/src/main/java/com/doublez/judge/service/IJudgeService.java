package com.doublez.judge.service;

import com.doublez.api.domain.dto.JudgeSubmitDTO;
import com.doublez.api.domain.vo.UserQuestionResultVO;

public interface IJudgeService {
    UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}
