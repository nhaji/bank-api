package com.bank.shared.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String REQUEST_TIME_HEADER = "X-Request-Time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID_HEADER, requestId);
        request.setAttribute(REQUEST_TIME_HEADER, LocalDateTime.now());
        response.setHeader(REQUEST_ID_HEADER, requestId);
        return true;
    }
}
