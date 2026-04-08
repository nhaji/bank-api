package com.bank.account.transactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.account.BaseApiTest;
import com.bank.account.management.api.AccountDto;
import com.bank.account.management.domain.AccountBusinessException;
import com.bank.account.transactions.api.DepositWithdrawDto;
import com.bank.account.transactions.api.StatementDto;
import com.bank.account.transactions.api.TransactionDto;
import com.bank.shared.dtos.ResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Account Transactions API Tests")
class TransactionControllerTest extends BaseApiTest {

  private Long accountId;

  @BeforeEach
  void createAccount() throws Exception {
    MvcResult response =
        mockMvc
            .perform(
                post("/accounts")
                    .with(auth(PRIMARY_USER))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
            .andExpect(status().isCreated())
            .andReturn();
    ResponseDTO<AccountDto> parsed =
        objectMapper.readValue(
            response.getResponse().getContentAsString(), new TypeReference<>() {});
    accountId = parsed.getData().getId();
  }

  @Nested
  @DisplayName("Deposit & Withdraw")
  class DepositWithdraw {

    @Test
    void shouldDepositMoney() throws Exception {
      mockMvc
          .perform(
              post("/accounts/{accountId}/deposit", accountId)
                  .with(auth(PRIMARY_USER))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new DepositWithdrawDto(5000L))))
          .andExpect(status().isOk());

      MvcResult statementResult =
          mockMvc
              .perform(get("/accounts/{accountId}/statement", accountId).with(auth(PRIMARY_USER)))
              .andExpect(status().isOk())
              .andReturn();

      ResponseDTO<StatementDto> statementResp =
          objectMapper.readValue(
              statementResult.getResponse().getContentAsString(), new TypeReference<>() {});
      List<TransactionDto> transactions = statementResp.getData().getTransactions();
      assertThat(transactions).hasSize(2);
      assertThat(transactions.get(1).getAmount()).isEqualTo(5000L);
      assertThat(statementResp.getRequestId()).isNotBlank();
    }

    @Test
    void shouldNotWithdrawMoreThanBalance() throws Exception {
      MvcResult result =
          mockMvc
              .perform(
                  post("/accounts/{accountId}/withdraw", accountId)
                      .with(auth(PRIMARY_USER))
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(new DepositWithdrawDto(1000L))))
              .andExpect(status().isBadRequest())
              .andReturn();
      assertThat(readErrorCode(result))
          .isEqualTo(AccountBusinessException.INSUFFICIENT_BALANCE_CODE);
    }
  }

  @Nested
  @DisplayName("GET /accounts/{accountId}/statement")
  class Statement {

    @Test
    void shouldRejectStatementForUnauthorizedAccount() throws Exception {
      MvcResult otherAccount =
          mockMvc
              .perform(
                  post("/accounts")
                      .with(auth(SECONDARY_USER))
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("{}"))
              .andExpect(status().isCreated())
              .andReturn();
      ResponseDTO<AccountDto> created =
          objectMapper.readValue(
              otherAccount.getResponse().getContentAsString(), new TypeReference<>() {});

      MvcResult result =
          mockMvc
              .perform(
                  get("/accounts/{accountId}/statement", created.getData().getId())
                      .with(auth(PRIMARY_USER)))
              .andExpect(status().isBadRequest())
              .andReturn();
      assertThat(readErrorCode(result))
          .isEqualTo(AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_CODE);
    }
  }
}
