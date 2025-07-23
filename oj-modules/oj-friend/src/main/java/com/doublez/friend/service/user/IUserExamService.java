package com.doublez.friend.service.user;

import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.exam.dto.ExamQueryDTO;

public interface IUserExamService {

    int enter(String token, Long examId);

    TableDataInfo list(ExamQueryDTO examQueryDTO);
}
