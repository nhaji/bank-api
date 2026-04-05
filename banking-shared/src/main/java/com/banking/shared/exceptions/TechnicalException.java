package com.banking.shared.exceptions;

public class TechnicalException extends BaseException {
    
    public TechnicalException(String code, String message) {
        super(code, message);
    }
}