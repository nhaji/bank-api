package com.banking.shared.exceptions;

import com.banking.shared.dtos.ErrorDTO;
import com.banking.shared.dtos.ResponseDTO;
import com.banking.shared.interceptors.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String INTERNAL_SERVER_ERROR_CODE = "SYS-500";
    public static final String INTERNAL_SERVER_ERROR_MSG_PREFIX = "Internal server error: ";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDTO<Void>> handleBusiness(BusinessException exception, HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ResponseDTO<Void>> handleTechnical(TechnicalException exception, HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Void>> handleGeneric(Exception exception, HttpServletRequest request) {
        return buildErrorResponse(new TechnicalException(INTERNAL_SERVER_ERROR_CODE, INTERNAL_SERVER_ERROR_MSG_PREFIX + exception.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ResponseDTO<Void>> buildErrorResponse(BaseException exception, HttpStatus status, HttpServletRequest request) {
        String requestId = (String) request.getAttribute(RequestInterceptor.REQUEST_ID_HEADER);
        LocalDateTime timestamp = (LocalDateTime) request.getAttribute(RequestInterceptor.REQUEST_TIME_HEADER);
        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .requestId(requestId)
                .timestamp(timestamp != null ? timestamp : LocalDateTime.now())
                .error(new ErrorDTO(exception.getCode(), exception.getMessage()))
                .build();
        return new ResponseEntity<>(response, status);
    }
}