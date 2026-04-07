package com.bank.shared.interceptors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestInterceptor extends OncePerRequestFilter {

  public static final String REQUEST_ID_HEADER = "X-Request-ID";
  public static final String REQUEST_TIME_HEADER = "X-Request-Time";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String requestId = UUID.randomUUID().toString();
    request.setAttribute(REQUEST_ID_HEADER, requestId);
    request.setAttribute(REQUEST_TIME_HEADER, LocalDateTime.now());
    response.setHeader(REQUEST_ID_HEADER, requestId);
    filterChain.doFilter(request, response);
  }
}
