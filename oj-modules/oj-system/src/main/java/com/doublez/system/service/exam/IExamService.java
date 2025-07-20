package com.doublez.system.service.exam;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.common.core.domain.R;
import com.doublez.system.domain.exam.dto.ExamAddDTO;
import com.doublez.system.domain.exam.dto.ExamEditDTO;
import com.doublez.system.domain.exam.dto.ExamQueryDTO;
import com.doublez.system.domain.exam.dto.ExamQuestionAddDTO;
import com.doublez.system.domain.exam.vo.ExamDetailVO;
import com.doublez.system.domain.exam.vo.ExamVO;

public interface IExamService {
    IPage<ExamVO> list(ExamQueryDTO examQueryDTO);

    int add(ExamAddDTO examQueryDTO);

    boolean questionAdd(ExamQuestionAddDTO examQuestionAddDTO);

    ExamDetailVO getDetail(Long examId);


    int edit(ExamEditDTO examEditDTO);

    int delete(Long examId);

    int questionDelete(Long examId, Long questionId);
}
