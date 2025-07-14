package com.doublez.common.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class R<T> {

    private int code;

    private String msg;

    private T data;
}
