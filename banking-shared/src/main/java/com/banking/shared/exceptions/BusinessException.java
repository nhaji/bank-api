package com.banking.shared.exceptions;

public class BusinessException extends BaseException {
    public BusinessException(String code, String message) {
        super(code, message);
    }
}