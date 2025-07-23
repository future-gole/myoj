package com.doublez.system.service.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.question.dto.QuestionAddDTO;
import com.doublez.system.domain.question.dto.QuestionEditDTO;
import com.doublez.system.domain.question.dto.QuestionQueryDTO;
import com.doublez.system.domain.question.vo.QuestionDetailVO;
import com.doublez.system.domain.question.vo.QuestionVO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IQuestionService {
    IPage<QuestionVO> list(QuestionQueryDTO queryDTO);

    boolean add(QuestionAddDTO addDTO);

    QuestionDetailVO getDetail(@NotNull Long questionId);

    int edit(@NotNull QuestionEditDTO editDTO);

    int delete(@NotNull Long questionId);
}
