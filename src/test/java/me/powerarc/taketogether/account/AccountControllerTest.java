package me.powerarc.taketogether.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.powerarc.taketogether.account.request.AccountDeleteRequest;
import me.powerarc.taketogether.account.request.AccountLoginRequest;
import me.powerarc.taketogether.account.request.AccountRegistRequest;
import me.powerarc.taketogether.account.request.AccountUpdateRequest;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
    }


    @Test
    public void regist() throws Exception {
        // Given
        AccountRegistRequest test = AccountRegistRequest.builder()
                .email("test@test.com")
                .password("1234")
                .name("test")
                .build();

        // Then
        mockMvc.perform(post("/account/regist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
        ;
    }

    @Test
    public void regist_fail_exist_email() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountRegistRequest test = AccountRegistRequest.builder()
                .email("test@test.com")
                .password("1234")
                .name("test")
                .build();

        // Then
        mockMvc.perform(post("/account/regist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("?????? ???????????? ???????????????.")));
    }

    @Test
    public void regist_fail_empty() throws Exception {
        // Given
        AccountRegistRequest test = AccountRegistRequest.builder()
                .build();

        // Then
        mockMvc.perform(post("/account/regist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("fail")))
                .andExpect(jsonPath("$.errors[*].field").exists())
                .andExpect(jsonPath("$.errors[*].objectName").exists())
                .andExpect(jsonPath("$.errors[*].code").exists())
                .andExpect(jsonPath("$.errors[*].defaultMessage").exists())
        ;
    }

    @Test
    public void login() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountLoginRequest accountLoginRequest = modelMapper.map(account, AccountLoginRequest.class);

        // Then
        mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("token").isNotEmpty())
        ;
    }

    @Test
    public void login_fail_wrong_password() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        account.setPassword(account.getPassword() + "test");
        AccountLoginRequest accountLoginRequest = modelMapper.map(account, AccountLoginRequest.class);

        // Then
        mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("??????????????? ???????????? ????????????.")))
        ;
    }

    @Test
    public void login_fail_empty_input() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountLoginRequest accountLoginRequest = new AccountLoginRequest();

        // Then
        mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("fail")))
                .andExpect(jsonPath("errors").exists())
        ;
    }

    @Test
    public void update() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        String token = jwtTokenProvider.createToken(account.getEmail());

        AccountUpdateRequest updateAccount = modelMapper.map(account, AccountUpdateRequest.class);
        updateAccount.setName("??????!");

        // Then
        mockMvc.perform(put("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(updateAccount)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
        ;
    }

    @Test
    public void update_wrong_password() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        String token = jwtTokenProvider.createToken(account.getEmail());

        AccountUpdateRequest updateAccount = modelMapper.map(account, AccountUpdateRequest.class);
        updateAccount.setName("??????!");
        updateAccount.setPassword(updateAccount.getPassword() + "wrong");

        // Then
        mockMvc.perform(put("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(updateAccount)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("??????????????? ???????????? ????????????.")))
        ;
    }

    @Test
    public void update_without_authentication() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountUpdateRequest updateAccount = modelMapper.map(account, AccountUpdateRequest.class);
        updateAccount.setName("??????!");

        // Then
        mockMvc.perform(put("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccount)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    public void delete() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        saveAccount(account);

        AccountDeleteRequest deleteAccount = modelMapper.map(account, AccountDeleteRequest.class);

        String token = jwtTokenProvider.createToken(account.getEmail());

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(deleteAccount)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
        ;

        assertThat(accountRepository.findByEmail(email).isPresent()).isFalse();
    }

    @Test
    public void delete_wrong_password() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        saveAccount(account);

        AccountDeleteRequest deleteAccount = modelMapper.map(account, AccountDeleteRequest.class);
        deleteAccount.setPassword(deleteAccount.getPassword() + "wrong");

        String token = jwtTokenProvider.createToken(account.getEmail());

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(deleteAccount)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("??????????????? ???????????? ????????????.")))
        ;

        assertThat(accountRepository.findByEmail(email)).isPresent();
    }

    @Test
    public void delete_without_authentication() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        saveAccount(account);

        AccountDeleteRequest deleteAccount = modelMapper.map(account, AccountDeleteRequest.class);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteAccount)))
                .andDo(print())
                .andExpect(status().isForbidden());

        assertThat(accountRepository.findByEmail(email).isPresent()).isTrue();
    }

    private void saveAccount(Account account) {
        AccountRegistRequest regist = modelMapper.map(account, AccountRegistRequest.class);
        accountService.createAccount(regist);
    }

    private Account makeAccount(String email) {
        Account account = Account.builder()
                .email(email)
                .password("pass")
                .hostEvents(new HashSet<>())
                .name("admin")
                .participantEvents(new HashSet<>())
                .roles(new HashSet<>(Set.of(AccountRole.ADMIN)))
                .build();
        return account;
    }
}