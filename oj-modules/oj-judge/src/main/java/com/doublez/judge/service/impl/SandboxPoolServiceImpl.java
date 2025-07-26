package com.doublez.judge.service.impl;

import com.doublez.judge.domain.SandBoxExecuteResult;
import com.doublez.judge.service.ISandboxPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SandboxPoolServiceImpl implements ISandboxPoolService {


    private String containerId;

    private String userCodeFileName;

    @Value("${sandbox.limit.time:5}")
    private Long timeLimit;

    @Override
    public SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList) {
        return null;
    }

}
