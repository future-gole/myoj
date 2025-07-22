package com.doublez.friend.service.exam;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.exam.dto.ExamQueryDTO;

public interface IExamService {
    IPage<?> list(ExamQueryDTO examQueryDTO);

    TableDataInfo redisList(ExamQueryDTO examQueryDTO);
}
