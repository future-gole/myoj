package com.doublez.friend.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.doublez.common.core.domain.vo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("tb_exam_question")
public class ExamQuestion extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long examQuestionId;

    private Long examId;

    private Long questionId;

    private Integer questionOrder;
}