package com.doublez.friend.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doublez.common.core.constants.CacheConstants;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.redis.service.RedisService;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.friend.domain.question.Question;
import com.doublez.friend.mapper.question.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionCacheManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private QuestionMapper questionMapper;

    public Long getListSize() {
        return redisService.getListSize(CacheConstants.QUESTION_LIST);
    }

    public Long getHostListSize() {
        return redisService.getListSize(CacheConstants.QUESTION_HOST_LIST);
    }

    //刷新题目列表缓存
    public void refreshCache() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId).orderByDesc(Question::getCreateTime));
        if (CollectionUtil.isEmpty(questionList)) {
            return;
        }
        List<Long> questionIdList = questionList.stream().map(Question::getQuestionId).toList();
        redisService.rightPushAll(CacheConstants.QUESTION_LIST, questionIdList);
    }

    public Long preQuestion(Long questionId) {
//        List<Long> list = redisService.getCacheListByRange(CacheConstants.QUESTION_LIST, 0, -1, Long.class);
        //获取当前题目的下标
        Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
        //判断是否存在
        if (index == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        //判断是否是第一题
        if (index == 0) {
            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
        }
        //获取上一题的数据
        return redisService.indexForList(CacheConstants.QUESTION_LIST, index - 1, Long.class);
    }

    public Object nextQuestion(Long questionId) {
        //获取当前题目的下标
        Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
        long lastIndex = getListSize() - 1;
        if (index == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        //判断是否是最后一题
        if (index == lastIndex) {
            throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
        }
        //获取下一题的数据
        return redisService.indexForList(CacheConstants.QUESTION_LIST, index + 1, Long.class);
    }

    public List<Long> getHostList() {
        return redisService.getCacheListByRange(CacheConstants.QUESTION_HOST_LIST,
                CacheConstants.DEFAULT_START, CacheConstants.DEFAULT_END, Long.class);
    }

    public void refreshHotQuestionList(List<Long> hotQuestionIdList) {
        if (CollectionUtil.isEmpty(hotQuestionIdList)) {
            return;
        }
        redisService.rightPushAll(CacheConstants.QUESTION_HOST_LIST, hotQuestionIdList);
    }
}
