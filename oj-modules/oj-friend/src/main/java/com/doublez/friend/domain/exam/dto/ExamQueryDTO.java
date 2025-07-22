package com.doublez.friend.domain.exam.dto;

import com.doublez.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    private String startTime;

    private String endTime;

    private Integer type; //0 未完善  1 历史竞赛
}