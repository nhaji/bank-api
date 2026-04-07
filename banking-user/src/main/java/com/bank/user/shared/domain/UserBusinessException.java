package com.bank.user.shared.domain;

public final class UserBusinessException {

  private UserBusinessException() {}

  public static final String EMAIL_ALREADY_EXIST_CODE = "USER-001";
  public static final String EMAIL_ALREADY_EXIST_MSG = "Email already in use";
  public static final String USER_NOT_FOUND_CODE = "USER-002";
  public static final String USER_NOT_FOUND_MSG = "User not found";
}
