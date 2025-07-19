package com.doublez.common.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQueryDTO {
    private Integer pageNum = 1;//第几页

    private Integer pageSize = 10;//每页几个数据
}
