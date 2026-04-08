package com.bank.user.management.api;

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
public class UpdateUserRequest {
  @NotBlank @Size(max = 50) private String firstName;

  @NotBlank @Size(max = 50) private String lastName;

  @NotBlank @Email @Size(max = 100) private String email;
}
