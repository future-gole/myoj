package com.doublez.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.doublez.api.domain.RemoteJudgeService;
import com.doublez.api.domain.domain.dto.JudgeSubmitDTO;
import com.doublez.api.domain.domain.vo.UserQuestionResultVO;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.enums.ProgramType;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.core.utils.ThreadLocalUtil;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.friend.domain.question.Question;
import com.doublez.friend.domain.question.QuestionCase;
import com.doublez.friend.domain.question.es.QuestionES;
import com.doublez.friend.domain.user.dto.UserSubmitDTO;
import com.doublez.friend.elasticsearch.QuestionRepository;
import com.doublez.friend.mapper.question.QuestionMapper;
import com.doublez.friend.mapper.user.UserSubmitMapper;
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
