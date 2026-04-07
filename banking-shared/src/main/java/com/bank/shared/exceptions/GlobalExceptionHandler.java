package com.bank.shared.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bank.shared.dtos.ErrorDTO;
import com.bank.shared.dtos.ResponseDTO;
import com.bank.shared.interceptors.RequestInterceptor;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String INTERNAL_SERVER_ERROR_CODE = "SYS-500";
    public static final String INTERNAL_SERVER_ERROR_MSG_PREFIX = "Internal server error: ";

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDTO<Void> handleBusiness(BusinessException exception, HttpServletRequest request) {
        return buildErrorResponse(exception, request);
    }

    @ExceptionHandler(TechnicalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDTO<Void> handleTechnical(TechnicalException exception, HttpServletRequest request) {
        return buildErrorResponse(exception, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDTO<Void> handleGeneric(Exception exception, HttpServletRequest request) {
        return buildErrorResponse(new TechnicalException(INTERNAL_SERVER_ERROR_CODE, INTERNAL_SERVER_ERROR_MSG_PREFIX + exception.getMessage()),
                request);
    }

    private ResponseDTO<Void> buildErrorResponse(BaseException exception, HttpServletRequest request) {
        String requestId = (String) request.getAttribute(RequestInterceptor.REQUEST_ID_HEADER);
        LocalDateTime timestamp = (LocalDateTime) request.getAttribute(RequestInterceptor.REQUEST_TIME_HEADER);
        return ResponseDTO.<Void>builder()
                .requestId(requestId)
                .timestamp(timestamp != null ? timestamp : LocalDateTime.now())
                .error(new ErrorDTO(exception.getCode(), exception.getMessage()))
                .build();
    }
}
