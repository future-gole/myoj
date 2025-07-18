package com.doublez.system.service.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.system.domain.question.dto.QuestionQueryDTO;
import com.doublez.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface IQuestionService {
    IPage<QuestionVO> list(QuestionQueryDTO queryDTO);
}
