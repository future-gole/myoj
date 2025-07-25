package com.doublez.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.question.Question;
import com.doublez.friend.domain.question.QuestionQueryDTO;
import com.doublez.friend.domain.question.es.QuestionES;
import com.doublez.friend.domain.question.vo.QuestionDetailVO;
import com.doublez.friend.domain.question.vo.QuestionVO;
import com.doublez.friend.elasticsearch.QuestionRepository;
import com.doublez.friend.manager.QuestionCacheManager;
import com.doublez.friend.mapper.question.QuestionMapper;
import com.doublez.friend.service.question.IQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionCacheManager questionCacheManager;

    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        long count = questionRepository.count();
        if (count <= 0) {
            if(!refreshQuestion()) return TableDataInfo.empty();
        }
        //指定es的排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //ES的from从0开始，需要-1
        Pageable pageable = PageRequest.of(questionQueryDTO.getPageNum() - 1, questionQueryDTO.getPageSize(), sort);
        Integer difficulty = questionQueryDTO.getDifficulty();
        String keyword = questionQueryDTO.getKeyword();
        Page<QuestionES> questionESPage;
        //无参查询
        if (difficulty == null && StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findAll(pageable);
        } else if (StrUtil.isEmpty(keyword)) {
            //通过difficult查询
            questionESPage = questionRepository.findQuestionByDifficulty(difficulty, pageable);
        } else if (difficulty == null) {
            //通过keyword进行查询
            questionESPage = questionRepository.findByTitleOrContent(keyword, keyword, pageable);
        } else {
            //所有参数查询
            questionESPage = questionRepository.findByTitleOrContentAndDifficulty(keyword, keyword, difficulty, pageable);
        }
        long total = questionESPage.getTotalElements();
        if (total <= 0) {
            return TableDataInfo.empty();
        }
        //获取具体的内容
        List<QuestionES> questionESList = questionESPage.getContent();
        //类型转化
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionESList, QuestionVO.class);
        return TableDataInfo.success(questionVOList, total);
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        if (questionES != null) {
            BeanUtil.copyProperties(questionES, questionDetailVO);
            return questionDetailVO;
        }
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return null;
        }
        refreshQuestion();
        BeanUtil.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public String preQuestion(Long questionId) {
        //查看redis中是否有缓存
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            questionCacheManager.refreshCache();
        }
        //获取上一条数据
        return questionCacheManager.preQuestion(questionId).toString();
    }

    @Override
    public String nextQuestion(Long questionId) {
        //查看redis中是否有缓存
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            questionCacheManager.refreshCache();
        }
        //获取下一条数据
        return questionCacheManager.nextQuestion(questionId).toString();
    }

    private boolean refreshQuestion() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>());
        if (CollectionUtil.isEmpty(questionList)) {
            return false;
        }
        List<QuestionES> questionESList = BeanUtil.copyToList(questionList, QuestionES.class);
        questionRepository.saveAll(questionESList);
        return true;
    }
}