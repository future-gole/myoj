package com.doublez.judge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doublez.api.domain.UserExeResult;
import com.doublez.api.domain.dto.JudgeSubmitDTO;
import com.doublez.api.domain.vo.UserQuestionResultVO;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.constants.JudgeConstants;
import com.doublez.common.core.enums.CodeRunStatus;
import com.doublez.judge.config.JudgeStrategyFactory;
import com.doublez.judge.domain.SandBoxExecuteResult;
import com.doublez.judge.domain.UserSubmit;
import com.doublez.judge.mapper.UserSubmitMapper;
import com.doublez.judge.service.IJudgeService;
import com.doublez.judge.template.AbstractJudgeTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JudgeServiceImpl implements IJudgeService {

//    @Autowired
//    private ISandboxPoolService sandboxPoolService;

    @Autowired
    private JudgeStrategyFactory judgeStrategyFactory;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Override
    public UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
        //获取判题策略对象
        AbstractJudgeTemplate strategy = judgeStrategyFactory.getStrategy(judgeSubmitDTO.getProgramType().getDesc());
        //调用容器池进行判题
        SandBoxExecuteResult sandBoxExecuteResult =
                strategy.judge(judgeSubmitDTO.getUserCode(), judgeSubmitDTO.getInputList(),judgeSubmitDTO.getTimeLimit());
        UserQuestionResultVO userQuestionResultVO = new UserQuestionResultVO();
        //返回判题结果
        //成功
        if (sandBoxExecuteResult != null && CodeRunStatus.SUCCEED.equals(sandBoxExecuteResult.getRunStatus())) {
            //比对直接结果  时间限制  空间限制的比对
            userQuestionResultVO = doJudge(judgeSubmitDTO, sandBoxExecuteResult, userQuestionResultVO);
        } else {
            //失败
            userQuestionResultVO.setPass(Constants.FALSE);
            if (sandBoxExecuteResult != null) {
                //放入错误信息
                userQuestionResultVO.setExeMessage(sandBoxExecuteResult.getExeMessage());
            } else {
                userQuestionResultVO.setExeMessage(CodeRunStatus.UNKNOWN_FAILED.getMsg());
            }
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
        }
        //存储用户代码数据到数据库
        saveUserSubmit(judgeSubmitDTO, userQuestionResultVO);
        log.info("判题逻辑结束，判题结果为： {} ", userQuestionResultVO.getPass());
        return userQuestionResultVO;
    }

    private UserQuestionResultVO doJudge(JudgeSubmitDTO judgeSubmitDTO,
                                         SandBoxExecuteResult sandBoxExecuteResult,
                                         UserQuestionResultVO userQuestionResultVO) {
        List<String> exeOutputList = sandBoxExecuteResult.getOutputList();
        //获取实际输出结果
        List<String> outputList = judgeSubmitDTO.getOutputList();
        //判断总数是否相同
        if (outputList.size() != exeOutputList.size()) {
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return userQuestionResultVO;
        }
        List<UserExeResult> userExeResultList = new ArrayList<>();
        //存储比较结果
        boolean passed = resultCompare(judgeSubmitDTO, exeOutputList, outputList, userExeResultList);
        return assembleUserQuestionResultVO(judgeSubmitDTO, sandBoxExecuteResult, userQuestionResultVO, userExeResultList, passed);
    }

    private UserQuestionResultVO assembleUserQuestionResultVO(JudgeSubmitDTO judgeSubmitDTO,
                                                              SandBoxExecuteResult sandBoxExecuteResult,
                                                              UserQuestionResultVO userQuestionResultVO,
                                                              List<UserExeResult> userExeResultList, boolean passed) {
        //比较结果存入userQuestionResultVO
        userQuestionResultVO.setUserExeResultList(userExeResultList);
        //未通过
        if (!passed) {
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return userQuestionResultVO;
        }
        //todo是否需要判断空间还是说docker会返回具体原因？
        if (sandBoxExecuteResult.getUseMemory() > judgeSubmitDTO.getSpaceLimit()) {
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_MEMORY.getMsg());
            return userQuestionResultVO;
        }
        //todo是否需要判断超时还是说docker会返回具体原因？
        if (sandBoxExecuteResult.getUseTime() > judgeSubmitDTO.getTimeLimit()) {
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_TIME.getMsg());
            return userQuestionResultVO;
        }
        userQuestionResultVO.setPass(Constants.TRUE);
        int score = judgeSubmitDTO.getDifficulty() * JudgeConstants.DEFAULT_SCORE;
        userQuestionResultVO.setScore(score);
        return userQuestionResultVO;
    }

    private boolean resultCompare(JudgeSubmitDTO judgeSubmitDTO, List<String> exeOutputList,
                                  List<String> outputList, List<UserExeResult> userExeResultList) {
        boolean passed = true;
        for (int index = 0; index < outputList.size(); index++) {
            String output = outputList.get(index);
            String exeOutPut = exeOutputList.get(index);
            String input = judgeSubmitDTO.getInputList().get(index);
            UserExeResult userExeResult = new UserExeResult();
            userExeResult.setInput(input);
            userExeResult.setOutput(output);
            userExeResult.setExeOutput(exeOutPut);
            userExeResultList.add(userExeResult);
            if (!output.equals(exeOutPut)) {
                passed = false;
                log.info("输入：{}， 期望输出：{}， 实际输出：{} ", input, output, exeOutputList);
            }
        }
        return passed;
    }

    private void saveUserSubmit(JudgeSubmitDTO judgeSubmitDTO, UserQuestionResultVO userQuestionResultVO) {
        UserSubmit userSubmit = new UserSubmit();
        BeanUtil.copyProperties(userQuestionResultVO, userSubmit);
        userSubmit.setUserId(judgeSubmitDTO.getUserId());
        userSubmit.setQuestionId(judgeSubmitDTO.getQuestionId());
        userSubmit.setExamId(judgeSubmitDTO.getExamId());
        userSubmit.setProgramType(judgeSubmitDTO.getProgramType().getValue());
        userSubmit.setUserCode(judgeSubmitDTO.getUserCode());
        userSubmit.setCaseJudgeRes(JSON.toJSONString(userQuestionResultVO.getUserExeResultList()));
        userSubmit.setCreateBy(judgeSubmitDTO.getUserId());
        userSubmitMapper.delete(new LambdaQueryWrapper<UserSubmit>()
                .eq(UserSubmit::getUserId, judgeSubmitDTO.getUserId())
                .eq(UserSubmit::getQuestionId, judgeSubmitDTO.getQuestionId())
                .isNull(judgeSubmitDTO.getExamId() == null, UserSubmit::getExamId)
                .eq(judgeSubmitDTO.getExamId() != null, UserSubmit::getExamId, judgeSubmitDTO.getExamId()));
        userSubmitMapper.insert(userSubmit);
    }
}
