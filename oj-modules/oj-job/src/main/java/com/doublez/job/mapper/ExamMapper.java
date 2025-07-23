package com.doublez.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doublez.job.domain.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
}
