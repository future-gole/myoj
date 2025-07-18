package com.doublez.common.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQueryDTO {
    private Integer pageNum = 10;

    private Integer pageSize = 1;
}
