package com.bank.user.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import com.bank.account.application.AccountFacade;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = UserModuleTestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseApiTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AccountFacade accountFacade;

    @Value("${test.user.email}")
    protected String testEmail;

    @Value("${test.user.unique-email}")
    protected String uniqueEmail;

    @Value("${test.user.password}")
    protected String testPassword;

    @Value("${test.user.first-name}")
    protected String testFirstName;

    @Value("${test.user.last-name}")
    protected String testLastName;

    protected RequestPostProcessor auth(String email) {
        return user(email).roles("USER");
    }

    protected String readErrorCode(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.path("error").path("code").asText();
    }
}
