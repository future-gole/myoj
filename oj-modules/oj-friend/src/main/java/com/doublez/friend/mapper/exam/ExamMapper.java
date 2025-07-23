package com.doublez.friend.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.friend.domain.exam.Exam;
import com.doublez.friend.domain.exam.dto.ExamQueryDTO;
import com.doublez.friend.domain.exam.vo.ExamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
    IPage<ExamVO> selectExamList(Page<ExamVO> page, @Param("query") ExamQueryDTO examQueryDTO);
}
