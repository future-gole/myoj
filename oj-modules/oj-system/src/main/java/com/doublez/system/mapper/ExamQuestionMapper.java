package com.doublez.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doublez.system.domain.exam.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
}
