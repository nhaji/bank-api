package com.bank.shared.interceptors;

import com.bank.shared.dtos.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return !returnType.hasMethodAnnotation(ExceptionHandler.class)
        && !returnType.getParameterType().equals(ResponseDTO.class)
        && !ResponseEntity.class.isAssignableFrom(returnType.getParameterType());
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
    String requestId = (String) servletRequest.getAttribute(RequestInterceptor.REQUEST_ID_HEADER);
    LocalDateTime timestamp =
        (LocalDateTime) servletRequest.getAttribute(RequestInterceptor.REQUEST_TIME_HEADER);
    if (timestamp == null) timestamp = LocalDateTime.now();

    if (body instanceof ResponseDTO<?>) {
      return body;
    }

    if (body == null && returnType.getMethod().getReturnType().equals(Void.TYPE)) {
      return ResponseDTO.builder().requestId(requestId).timestamp(timestamp).data(null).build();
    }

    return ResponseDTO.builder().requestId(requestId).timestamp(timestamp).data(body).build();
  }
}
