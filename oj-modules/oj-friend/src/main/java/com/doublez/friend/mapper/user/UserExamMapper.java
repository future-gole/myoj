package com.doublez.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.friend.domain.exam.vo.ExamRankVO;
import com.doublez.friend.domain.exam.vo.ExamVO;
import com.doublez.friend.domain.user.UserExam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserExamMapper extends BaseMapper<UserExam> {

    IPage<ExamVO> selectUserExamList(IPage<ExamVO> page,Long userId);

    List<ExamVO> selectUserExamList(Long userId);

    List<ExamRankVO> selectExamRankList(Long examId);

}