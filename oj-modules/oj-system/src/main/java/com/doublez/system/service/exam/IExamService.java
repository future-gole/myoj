package com.doublez.system.service.exam;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.system.domain.exam.dto.ExamAddDTO;
import com.doublez.system.domain.exam.dto.ExamQueryDTO;
import com.doublez.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {
    IPage<ExamVO> list(ExamQueryDTO examQueryDTO);

    int add(ExamAddDTO examQueryDTO);
}
