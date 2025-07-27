package com.doublez.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.doublez.api.RemoteJudgeService;
import com.doublez.api.domain.UserExeResult;
import com.doublez.api.domain.dto.JudgeSubmitDTO;
import com.doublez.api.domain.vo.UserQuestionResultVO;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.enums.ProgramType;
import com.doublez.common.core.enums.QuestionResType;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.core.utils.ThreadLocalUtil;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.friend.domain.question.Question;
import com.doublez.friend.domain.question.QuestionCase;
import com.doublez.friend.domain.question.es.QuestionES;
import com.doublez.friend.domain.user.UserSubmit;
import com.doublez.friend.domain.user.dto.UserSubmitDTO;
import com.doublez.friend.elasticsearch.QuestionRepository;
import com.doublez.friend.mapper.question.QuestionMapper;
import com.doublez.friend.mapper.user.UserSubmitMapper;
import com.doublez.friend.rabbit.JudgeProducer;
import com.doublez.friend.service.user.IUserQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserQuestionServiceImpl implements IUserQuestionService {


    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    @Autowired
    private JudgeProducer judgeProducer;


    @Override
    public R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)) {
            //按照java逻辑处理
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(submitDTO);
            //openfeign调用判题服务
            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO);
        }
        throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
    }

    @Override
    public boolean rabbitSubmit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)) {
            //按照java逻辑处理
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(submitDTO);
            judgeProducer.produceMsg(judgeSubmitDTO);
            return true;
        }
        throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
    }

    @Override
    public UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime) {
        //获取用户id
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        //获取判题信息
        UserSubmit userSubmit = userSubmitMapper.selectCurrentUserSubmit(userId, examId, questionId, currentTime);
        UserQuestionResultVO resultVO = new UserQuestionResultVO();
        //判断判题是否完成
        if (userSubmit == null) {
            resultVO.setPass(QuestionResType.IN_JUDGE.getValue());
        } else {
            resultVO.setPass(userSubmit.getPass());
            resultVO.setExeMessage(userSubmit.getExeMessage());
            if (StrUtil.isNotEmpty(userSubmit.getCaseJudgeRes())) {
                resultVO.setUserExeResultList(JSON.parseArray(userSubmit.getCaseJudgeRes(), UserExeResult.class));
            }
        }
        return resultVO;
    }

    private JudgeSubmitDTO assembleJudgeSubmitDTO(UserSubmitDTO submitDTO) {
        Long questionId = submitDTO.getQuestionId();
        //根据questionId查询es
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        JudgeSubmitDTO judgeSubmitDTO = new JudgeSubmitDTO();
        if (questionES != null) {
            BeanUtil.copyProperties(questionES, judgeSubmitDTO);
        } else {
            //为空查询数据库并且回填es
            Question question = questionMapper.selectById(questionId);
            BeanUtil.copyProperties(question, judgeSubmitDTO);
            questionES = new QuestionES();
            BeanUtil.copyProperties(question, questionES);
            questionRepository.save(questionES);
        }
        //复制数据至judgeSubmitDTO
        judgeSubmitDTO.setUserId(ThreadLocalUtil.get(Constants.USER_ID, Long.class));
        judgeSubmitDTO.setExamId(submitDTO.getExamId());
        judgeSubmitDTO.setProgramType(submitDTO.getProgramType());
        //放置拼接之后的完整代码
        judgeSubmitDTO.setUserCode(codeConnect(submitDTO.getUserCode(), questionES.getMainFuc()));
        //获取input用例
        List<QuestionCase> questionCaseList = JSONUtil.toList(questionES.getQuestionCase(), QuestionCase.class);
        List<String> inputList = questionCaseList.stream().map(QuestionCase::getInput).toList();
        judgeSubmitDTO.setInputList(inputList);
        //todo 需要获取output用例吗？
        List<String> outputList = questionCaseList.stream().map(QuestionCase::getOutput).toList();
        judgeSubmitDTO.setOutputList(outputList);
        return judgeSubmitDTO;
    }

    private String codeConnect(String userCode, String mainFunc) {
        String targetCharacter = "}";
        int targetLastIndex = userCode.lastIndexOf(targetCharacter);
        if (targetLastIndex != -1) {
            return userCode.substring(0, targetLastIndex) + "\n" + mainFunc + "\n" + userCode.substring(targetLastIndex);
        }
        throw new ServiceException(ResultCode.FAILED);
    }
}
