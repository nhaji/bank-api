package com.bank.account.domain;

public final class AccountBusinessException {

  private AccountBusinessException() {}

  public static final String INSUFFICIENT_BALANCE_CODE = "ACCOUNT-001";
  public static final String INSUFFICIENT_BALANCE_MSG = "Insufficient balance";
  public static final String NEGATIVE_AMOUNT_DEPOSIT_CODE = "ACCOUNT-002";
  public static final String NEGATIVE_AMOUNT_DEPOSIT_MSG = "Deposit amount must be positive";
  public static final String NEGATIVE_AMOUNT_WITHDRAW_CODE = "ACCOUNT-003";
  public static final String NEGATIVE_AMOUNT_WITHDRAW_MSG = "Withdraw amount must be positive";
  public static final String ACCOUNT_ACCES_UNAUTHORIZED_CODE = "ACCOUNT-004";
  public static final String ACCOUNT_ACCES_UNAUTHORIZED_MSG = "You cannot access this account";
  public static final String ACCOUNT_NOT_FOUND_CODE = "ACCOUNT-005";
  public static final String ACCOUNT_NOT_FOUND_MSG = "Account not found";
}
