package com.banking.account.exceptions;

public class AccountBusinessException {

    public static final String MORE_THAN_BALANCE_CODE = "ACCOUNT-001";
    public static final String MORE_THAN_BALANCE_MSG = "You cannot withdraw ore than your balance";
    public static final String NEGATIVE_AMOUNT_DEPOSIT_CODE = "ACCOUNT-002";
    public static final String NEGATIVE_AMOUNT_DEPOSIT_MSG = "You cannot deposit a negative amount";
    public static final String ACCOUNT_ACCES_UNAUTHORIZED_CODE = "ACCOUNT-003";
    public static final String ACCOUNT_ACCES_UNAUTHORIZED_MSG = "You cannot access this account";
;    
}
