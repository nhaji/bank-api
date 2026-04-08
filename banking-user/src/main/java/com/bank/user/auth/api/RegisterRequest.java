package com.bank.user.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  @NotBlank @Size(max = 50) private String firstname;

  @NotBlank @Size(max = 50) private String lastname;

  @NotBlank @Email @Size(max = 100) private String email;

  @NotBlank @Size(min = 8, max = 100) private String password;
}
