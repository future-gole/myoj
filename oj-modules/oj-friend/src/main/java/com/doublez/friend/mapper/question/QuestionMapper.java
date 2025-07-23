package com.doublez.friend.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doublez.friend.domain.question.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
