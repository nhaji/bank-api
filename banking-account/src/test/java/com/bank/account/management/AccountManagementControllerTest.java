package com.bank.account.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.account.BaseApiTest;
import com.bank.account.api.AccountDto;
import com.bank.shared.dtos.ResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Account Management API Tests")
class AccountManagementControllerTest extends BaseApiTest {

  @BeforeEach
  void ensureAccountExists() throws Exception {
    mockMvc
        .perform(
            post("/accounts")
                .with(auth(PRIMARY_USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isCreated());
  }

  @Nested
  @DisplayName("POST /accounts")
  class CreateAccount {

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
      assertThat(afterResp.getData()).hasSize(2);
      assertThat(afterResp.getRequestId()).isNotBlank();
    }
  }

  @Nested
  @DisplayName("GET /accounts")
  class ListAccounts {

    @Test
    void shouldListAccountsForCurrentUser() throws Exception {
      MvcResult result =
          mockMvc
              .perform(get("/accounts").with(auth(PRIMARY_USER)))
              .andExpect(status().isOk())
              .andReturn();

      ResponseDTO<List<AccountDto>> response =
          objectMapper.readValue(
              result.getResponse().getContentAsString(), new TypeReference<>() {});
      assertThat(response.getData()).hasSize(1);
      assertThat(response.getRequestId()).isNotBlank();
    }
  }
}
