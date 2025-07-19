package com.doublez.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.system.domain.exam.Exam;
import com.doublez.system.domain.exam.dto.ExamAddDTO;
import com.doublez.system.domain.exam.dto.ExamQueryDTO;
import com.doublez.system.domain.exam.vo.ExamVO;
import com.doublez.system.mapper.ExamMapper;
import com.doublez.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService implements IExamService {
    @Autowired
    private ExamMapper examMapper;

    @Override
    public IPage<ExamVO> list(ExamQueryDTO examQueryDTO) {
        Page<ExamVO> page = new Page<>(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(page, examQueryDTO);
    }

    @Override
    public int add(ExamAddDTO examAddDTO) {
        Long num = examMapper.selectCount(new LambdaQueryWrapper<Exam>().eq(Exam::getTitle, examAddDTO.getTitle()));
        if (num > 0) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        if(examAddDTO.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        if(examAddDTO.getStartTime().isAfter(examAddDTO.getEndTime())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
        Exam exam = BeanUtil.copyProperties(examAddDTO, Exam.class);
        return examMapper.insert(exam);
    }
}
