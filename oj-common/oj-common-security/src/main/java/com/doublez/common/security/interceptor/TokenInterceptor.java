package com.doublez.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.constants.HttpConstants;
import com.doublez.common.core.utils.ThreadLocalUtil;
import com.doublez.common.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long userId = tokenService.getUserId(token, secret);
        //存入userId
        ThreadLocalUtil.set(Constants.USER_ID, userId);
        tokenService.extendToken(token, secret);
        return true;
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpConstants.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}
