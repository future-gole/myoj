package com.doublez.system.service.exam.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.system.domain.exam.dto.ExamQueryDTO;
import com.doublez.system.domain.exam.vo.ExamVO;
import com.doublez.system.mapper.ExamMapper;
import com.doublez.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService implements IExamService {
    @Autowired
    private ExamMapper examMapper;

    @Override
    public IPage<ExamVO> list(ExamQueryDTO examQueryDTO) {
        Page<ExamVO> page = new Page<>(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        IPage<ExamVO> examVOIPage = examMapper.selectExamList(page, examQueryDTO);
        return examVOIPage;
    }
}
