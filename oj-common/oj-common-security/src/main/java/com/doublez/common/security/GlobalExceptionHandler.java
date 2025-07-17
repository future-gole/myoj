package com.doublez.common.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.security.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * * 全局异常处理器
 * */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * * 请求方式不支持
     * */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return R.fail(ResultCode.ERROR);
    }

    /**
     * * 拦截运行时异常
     * */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生异常", requestURI, e);
        return R.fail(ResultCode.ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    public R<?> handleServiceException(ServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        ResultCode resultCode = e.getResultCode();
        log.error("请求地址'{}',发生业务异常:{}", requestURI, e.getMessage(),e);
        return R.fail(resultCode);
    }

    /**
     * * 系统异常
     * */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生异常.", requestURI, e);
        return R.fail(ResultCode.ERROR);
    }
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        log.error(e.getMessage());
        String message = join(e.getAllErrors(),
                DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return R.fail(ResultCode.FAILED_PARAMS_VALIDATE.getCode(), message);
    }

    private <E> String join(Collection<E> collection, Function<E, String>
            function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StrUtil.EMPTY;
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }
//    /**
//     * 参数校验异常,这样如果有多个错误需要返回多个字段，如果只返回第一个错误的话可以考虑
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public R<?> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
//        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
//        Map<String, String> error = new HashMap<>();
//        for (FieldError fieldError : fieldErrors) {
//            error.put(fieldError.getField(), fieldError.getDefaultMessage());
//        }
//        return R.fail(error,ResultCode.ERROR);
//    }
}