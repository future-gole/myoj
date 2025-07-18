package com.doublez.system.domain.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.doublez.common.core.domain.vo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("tb_question")
@Getter
@Setter
public class Question extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long questionId;

    private String title;

    /**
     * 难度： 1 简单，2中等，3困难
     */
    private Integer difficulty;


    private Integer timeLimit;


    private Integer spaceLimit;


    private String content;


    private String questionCase;

    private String defaultCode;

    private String mainFuc;
}
