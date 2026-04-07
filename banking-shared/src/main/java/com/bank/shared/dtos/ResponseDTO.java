package com.bank.shared.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
  private String requestId;
  private LocalDateTime timestamp;
  private T data;
  private ErrorDTO error;
}
