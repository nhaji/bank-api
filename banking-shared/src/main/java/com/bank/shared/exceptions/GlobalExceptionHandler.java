package com.bank.shared.exceptions;

import com.bank.shared.dtos.ErrorDTO;
import com.bank.shared.dtos.ResponseDTO;
import com.bank.shared.interceptors.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  public static final String INTERNAL_SERVER_ERROR_CODE = "SYS-500";
  public static final String INTERNAL_SERVER_ERROR_MSG_PREFIX = "Internal server error: ";
  public static final String VALIDATION_ERROR_CODE = "VALIDATION-001";

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseDTO<Void> handleBusiness(BusinessException exception, HttpServletRequest request) {
    return buildErrorResponse(exception, request);
  }

  @ExceptionHandler(TechnicalException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseDTO<Void> handleTechnical(
      TechnicalException exception, HttpServletRequest request) {
    return buildErrorResponse(exception, request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseDTO<Void> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
    return buildErrorResponse(VALIDATION_ERROR_CODE, message, request);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseDTO<Void> handleConstraintViolation(
      ConstraintViolationException exception, HttpServletRequest request) {
    String message =
        exception.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining("; "));
    return buildErrorResponse(VALIDATION_ERROR_CODE, message, request);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseDTO<Void> handleGeneric(Exception exception, HttpServletRequest request) {
    return buildErrorResponse(
        new TechnicalException(
            INTERNAL_SERVER_ERROR_CODE, INTERNAL_SERVER_ERROR_MSG_PREFIX + exception.getMessage()),
        request);
  }

  private ResponseDTO<Void> buildErrorResponse(
      BaseException exception, HttpServletRequest request) {
    return buildErrorResponse(exception.getCode(), exception.getMessage(), request);
  }

  private ResponseDTO<Void> buildErrorResponse(
      String code, String message, HttpServletRequest request) {
    String requestId = (String) request.getAttribute(RequestInterceptor.REQUEST_ID_HEADER);
    LocalDateTime timestamp =
        (LocalDateTime) request.getAttribute(RequestInterceptor.REQUEST_TIME_HEADER);
    return ResponseDTO.<Void>builder()
        .requestId(requestId)
        .timestamp(timestamp != null ? timestamp : LocalDateTime.now())
        .error(new ErrorDTO(code, message))
        .build();
  }
}
