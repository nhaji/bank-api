package com.bank.account;

import com.bank.account.dtos.*;
import com.bank.shared.dtos.ResponseDTO;
import com.bank.account.exceptions.AccountBusinessException;
import com.bank.account.services.AccountService;
import com.bank.shared.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Account API Tests")
class AccountControllerTest extends BaseApiTest {

    private String jwtToken;
    private Long firstAccountId;

    @BeforeEach
    void setUp() throws Exception {
        jwtToken = registerAndLogin();

        // Retrieve user's accounts to get first account ID
        MvcResult accountsResult = mockMvc.perform(get("/accounts")
                        .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDTO<List<AccountDto>> accountsResp = objectMapper.readValue(
                accountsResult.getResponse().getContentAsString(),
                new TypeReference<>() {});
        firstAccountId = accountsResp.getData().get(0).getId();
    }

    @Nested
    @DisplayName("POST /accounts")
    class CreateAccountTests {

        @Test
        void shouldCreateAdditionalAccountForUser() throws Exception {
            // Initially one account
            MvcResult before = mockMvc.perform(get("/accounts")
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();
            ResponseDTO<List<AccountDto>> beforeResp = objectMapper.readValue(
                    before.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(beforeResp.getData()).hasSize(1);

            // Create new account
            mockMvc.perform(post("/accounts")
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isCreated());

            // Now two accounts
            MvcResult after = mockMvc.perform(get("/accounts")
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();
            ResponseDTO<List<AccountDto>> afterResp = objectMapper.readValue(
                    after.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(afterResp.getData()).hasSize(2);
            assertThat(afterResp.getData().get(1).getAccountNumber()).startsWith(AccountService.ACCOUNT_PREFIX);
        }
    }

    @Nested
    @DisplayName("GET /accounts")
    class GetAccountsTests {

        @Test
        void shouldReturnAllAccountsForAuthenticatedUser() throws Exception {
            // Create a second account
            mockMvc.perform(post("/accounts")
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isCreated());

            MvcResult result = mockMvc.perform(get("/accounts")
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseDTO<List<AccountDto>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(response.getData()).hasSize(2);
            assertThat(response.getData().get(0).getId()).isNotNull();
            assertThat(response.getData().get(0).getAccountNumber()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Deposit & Withdraw")
    class TransactionTests {

        @Test
        void shouldDepositMoney() throws Exception {
            DepositWithdrawRequest request = new DepositWithdrawRequest(5000L);
            mockMvc.perform(post("/accounts/{accountId}/deposit", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // Verify via statement
            MvcResult statementResult = mockMvc.perform(get("/accounts/{accountId}/statement", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken))
                    .andReturn();
            ResponseDTO<StatementDto> statementResp = objectMapper.readValue(
                    statementResult.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            List<TransactionDto> txns = statementResp.getData().getTransactions();
            assertThat(txns).hasSize(2);
            assertThat(txns.get(1).getAmount()).isEqualTo(5000L);
            assertThat(txns.get(1).getBalance()).isEqualTo(5000L);
        }

        @Test
        void shouldWithdrawMoney() throws Exception {
            // Deposit first
            mockMvc.perform(post("/accounts/{accountId}/deposit", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new DepositWithdrawDto(5000L))))
                    .andExpect(status().isOk());

            // Withdraw
            mockMvc.perform(post("/accounts/{accountId}/withdraw", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new DepositWithdrawDto(2000L))))
                    .andExpect(status().isOk());

            // Check statement
            MvcResult result = mockMvc.perform(get("/accounts/{accountId}/statement", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken))
                    .andReturn();
            ResponseDTO<StatementDto> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            List<TransactionDto> txns = response.getData().getTransactions();
            assertThat(txns).hasSize(3);
            assertThat(txns.get(2).getAmount()).isEqualTo(-2000L);
            assertThat(txns.get(2).getBalance()).isEqualTo(3000L);
        }

        @Test
        void shouldNotWithdrawMoreThanBalance() throws Exception {
            DepositWithdrawDto request = new DepositWithdrawDto(1000L);
            MvcResult result = mockMvc.perform(post("/accounts/{accountId}/withdraw", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ResponseDTO<Void> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(response.getError().getCode()).isEqualTo(AccountBusinessException.INSUFFICIENT_BALANCE_CODE);
        }

        @Test
        void shouldNotDepositNegativeAmount() throws Exception {
            DepositWithdrawDto request = new DepositWithdrawDto(-100L);
            MvcResult result = mockMvc.perform(post("/accounts/{accountId}/deposit", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ResponseDTO<Void> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(response.getError().getCode()).isEqualTo(AccountBusinessException.NEGATIVE_AMOUNT_DEPOSIT_CODE);
        }
    }

    @Nested
    @DisplayName("GET /accounts/{accountId}/statement")
    class StatementTests {

        @Test
        void shouldPrintStatementWithAllTransactions() throws Exception {
            // Perform multiple operations
            mockMvc.perform(post("/accounts/{accountId}/deposit", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new DepositWithdrawDto(15000L))))
                    .andExpect(status().isOk());
            mockMvc.perform(post("/accounts/{accountId}/withdraw", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new DepositWithdrawDto(2500L))))
                    .andExpect(status().isOk());

            MvcResult result = mockMvc.perform(get("/accounts/{accountId}/statement", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseDTO<StatementDto> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            StatementDto statement = response.getData();
            assertThat(statement.getAccountNumber()).isNotBlank();
            List<TransactionDto> txns = statement.getTransactions();
            assertThat(txns).hasSize(3);
            assertThat(txns.get(0).getAmount()).isZero();
            assertThat(txns.get(0).getBalance()).isZero();
            assertThat(txns.get(1).getAmount()).isEqualTo(15000L);
            assertThat(txns.get(1).getBalance()).isEqualTo(15000L);
            assertThat(txns.get(2).getAmount()).isEqualTo(-2500L);
            assertThat(txns.get(2).getBalance()).isEqualTo(12500L);
        }

        @Test
        void shouldRejectStatementForUnauthorizedAccount() throws Exception {
            // Create another user and get its account ID
            String otherUserEmail = "user1@example.com";
            String otherUserPassword = "ser123";
            RegisterRequest registerOther = new RegisterRequest(otherUserEmail, otherUserPassword);
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerOther)))
                    .andExpect(status().isCreated());

            LoginRequest loginOther = new LoginRequest(otherUserEmail, otherUserPassword);
            MvcResult loginResult = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginOther)))
                    .andExpect(status().isOk())
                    .andReturn();
            ResponseDTO<LoginResponse> loginResp = objectMapper.readValue(
                    loginResult.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            String otherToken = loginResp.getData().getToken();

            // Try to access first user's account
            mockMvc.perform(get("/accounts/{accountId}/statement", firstAccountId)
                            .header(JwtAuthenticationFilter.TOKEN_HEADER, JwtAuthenticationFilter.TOKEN_PREFIX + otherToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        ResponseDTO<Void> resp = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                new TypeReference<>() {});
                        assertThat(resp.getError().getCode()).isEqualTo(AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_CODE

                        );
                    });
        }
    }
}