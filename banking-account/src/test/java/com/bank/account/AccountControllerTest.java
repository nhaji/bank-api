package com.bank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.account.api.AccountDto;
import com.bank.account.api.DepositWithdrawDto;
import com.bank.account.api.StatementDto;
import com.bank.account.api.TransactionDto;
import com.bank.account.application.AccountService;
import com.bank.account.domain.AccountBusinessException;
import com.bank.shared.dtos.ResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Account API Tests")
class AccountControllerTest extends BaseApiTest {

  private Long firstAccountId;

  @BeforeEach
  void setUp() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/accounts")
                    .with(auth(PRIMARY_USER))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
            .andExpect(status().isCreated())
            .andReturn();

    ResponseDTO<AccountDto> response =
        objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertThat(response.getRequestId()).isNotBlank();
    firstAccountId = response.getData().getId();
  }

  @Nested
  @DisplayName("POST /accounts")
  class CreateAccountTests {

    @Test
    void shouldCreateAdditionalAccountForUser() throws Exception {
      MvcResult before =
          mockMvc
              .perform(get("/accounts").with(auth(PRIMARY_USER)))
              .andExpect(status().isOk())
              .andReturn();
      ResponseDTO<List<AccountDto>> beforeResp =
          objectMapper.readValue(
              before.getResponse().getContentAsString(), new TypeReference<>() {});
      assertThat(beforeResp.getData()).hasSize(1);

      mockMvc
          .perform(
              post("/accounts")
                  .with(auth(PRIMARY_USER))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isCreated());

      MvcResult after =
          mockMvc
              .perform(get("/accounts").with(auth(PRIMARY_USER)))
              .andExpect(status().isOk())
              .andReturn();
      ResponseDTO<List<AccountDto>> afterResp =
          objectMapper.readValue(
              after.getResponse().getContentAsString(), new TypeReference<>() {});
      assertThat(afterResp.getRequestId()).isNotBlank();
      assertThat(afterResp.getData()).hasSize(2);
      assertThat(afterResp.getData().get(1).getAccountNumber())
          .startsWith(AccountService.ACCOUNT_PREFIX);
    }
  }

  @Nested
  @DisplayName("Deposit & Withdraw")
  class TransactionTests {

    @Test
    void shouldDepositMoney() throws Exception {
      mockMvc
          .perform(
              post("/accounts/{accountId}/deposit", firstAccountId)
                  .with(auth(PRIMARY_USER))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new DepositWithdrawDto(5000L))))
          .andExpect(status().isOk());

      MvcResult statementResult =
          mockMvc
              .perform(
                  get("/accounts/{accountId}/statement", firstAccountId).with(auth(PRIMARY_USER)))
              .andExpect(status().isOk())
              .andReturn();
      ResponseDTO<StatementDto> statementResp =
          objectMapper.readValue(
              statementResult.getResponse().getContentAsString(), new TypeReference<>() {});
      assertThat(statementResp.getRequestId()).isNotBlank();
      List<TransactionDto> txns = statementResp.getData().getTransactions();
      assertThat(txns).hasSize(2);
      assertThat(txns.get(1).getAmount()).isEqualTo(5000L);
      assertThat(txns.get(1).getBalance()).isEqualTo(5000L);
    }

    @Test
    void shouldNotWithdrawMoreThanBalance() throws Exception {
      MvcResult result =
          mockMvc
              .perform(
                  post("/accounts/{accountId}/withdraw", firstAccountId)
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
  class StatementTests {

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
