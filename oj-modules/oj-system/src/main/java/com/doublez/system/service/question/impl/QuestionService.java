package com.doublez.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.system.domain.question.Question;
import com.doublez.system.domain.question.dto.QuestionAddDTO;
import com.doublez.system.domain.question.dto.QuestionEditDTO;
import com.doublez.system.domain.question.dto.QuestionQueryDTO;
import com.doublez.system.domain.question.es.QuestionES;
import com.doublez.system.domain.question.vo.QuestionDetailVO;
import com.doublez.system.domain.question.vo.QuestionVO;
import com.doublez.system.elasticsearch.QuestionRepository;
import com.doublez.system.mapper.QuestionMapper;
import com.doublez.system.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public IPage<QuestionVO> list(QuestionQueryDTO queryDTO) {
        // 1. 创建 Mybatis-Plus 的 Page 对象，传入当前页码和每页数量
        Page<QuestionVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 将 Page 对象作为第一个参数传给 Mapper 方法
        // Mybatis-Plus 会自动识别这个参数，并进行分页
        return questionMapper.selectQuestionList(page, queryDTO);
    }

    @Override
    public boolean add(QuestionAddDTO addDTO) {
        Long count = questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getTitle, addDTO.getTitle()));
        if (count > 0) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        Question question = BeanUtil.copyProperties(addDTO, Question.class);
        if(questionMapper.insert(question) < 0){
            return false;
        }
        QuestionES questionES = BeanUtil.copyProperties(question, QuestionES.class);
        questionRepository.save(questionES);
        return true;
    }

    @Override
    public QuestionDetailVO getDetail(Long questionId) {
        Question question = getQuestionById(questionId);
        return BeanUtil.copyProperties(question, QuestionDetailVO.class);
    }

    @Override
    public int edit(QuestionEditDTO editDTO) {
        Question question = getQuestionById(editDTO.getQuestionId());
        BeanUtil.copyProperties(editDTO, question);
        QuestionES questionES = BeanUtil.copyProperties(editDTO, QuestionES.class);
        questionRepository.save(questionES);
        return questionMapper.updateById(question);
    }

    @Override
    public int delete(Long questionId) {
        questionRepository.deleteById(questionId);
        return questionMapper.deleteById(questionId);
    }

    private Question getQuestionById(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        return question;
    }
}
