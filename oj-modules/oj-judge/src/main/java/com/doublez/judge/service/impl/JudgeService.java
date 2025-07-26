package com.doublez.judge.service.impl;

import com.doublez.api.domain.domain.dto.JudgeSubmitDTO;
import com.doublez.api.domain.domain.vo.UserQuestionResultVO;
import com.doublez.judge.mapper.UserSubmitMapper;
import com.doublez.judge.service.IJudgeService;
import com.doublez.judge.service.ISandboxPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JudgeService implements IJudgeService {


    @Autowired
    private ISandboxPoolService sandboxPoolService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Override
    public UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
            return null;
    }
}
