package com.bank.account.transactions.api;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
  private Long amount;
  private String type;
  private Long balance;
  private LocalDateTime date;
}
