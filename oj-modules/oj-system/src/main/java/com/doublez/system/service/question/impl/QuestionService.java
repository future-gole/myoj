package com.doublez.system.service.question.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.system.domain.question.dto.QuestionQueryDTO;
import com.doublez.system.domain.question.vo.QuestionVO;
import com.doublez.system.mapper.QuestionMapper;
import com.doublez.system.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public IPage<QuestionVO> list(QuestionQueryDTO queryDTO) {
        // 1. 创建 Mybatis-Plus 的 Page 对象，传入当前页码和每页数量
        Page<QuestionVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 将 Page 对象作为第一个参数传给 Mapper 方法
        // Mybatis-Plus 会自动识别这个参数，并进行分页
        return questionMapper.selectQuestionList(page, queryDTO);
    }
}
