package me.powerarc.taketogether.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.powerarc.taketogether.account.dto.AccountDeleteDto;
import me.powerarc.taketogether.account.dto.AccountLoginDto;
import me.powerarc.taketogether.account.dto.AccountRegistDto;
import me.powerarc.taketogether.account.dto.AccountUpdateDto;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
        AccountRegistDto test = AccountRegistDto.builder()
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

        ;
    }

    @Test
    public void regist_fail_exist_email() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountRegistDto test = AccountRegistDto.builder()
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
                .andExpect(jsonPath("message", is("이미 존재하는 이메일 입니다.")));
    }

    @Test
    public void regist_fail_empty() throws Exception {
        // Given
        AccountRegistDto test = AccountRegistDto.builder()
                .build();

        // Then
        mockMvc.perform(post("/account/regist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field").exists())
                .andExpect(jsonPath("$[*].objectName").exists())
                .andExpect(jsonPath("$[*].code").exists())
                .andExpect(jsonPath("$[*].defaultMessage").exists())
        ;
    }

    @Test
    public void login() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountLoginDto accountLoginDto = modelMapper.map(account, AccountLoginDto.class);

        // Then
        mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountLoginDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void login_fail_wrong_password() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        account.setPassword(account.getPassword() + "test");
        AccountLoginDto accountLoginDto = modelMapper.map(account, AccountLoginDto.class);

        // Then
        mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountLoginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("fail")))
        ;
    }

    @Test
    public void login_fail_empty_input() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountLoginDto accountLoginDto = new AccountLoginDto();

        // Then
        mockMvc.perform(post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountLoginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void update() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        String token = jwtTokenProvider.createToken(account.getEmail());

        AccountUpdateDto updateAccount = modelMapper.map(account, AccountUpdateDto.class);
        updateAccount.setName("핳핳!");

        // Then
        mockMvc.perform(put("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(updateAccount)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void update_without_authentication() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        saveAccount(account);

        AccountUpdateDto updateAccount = modelMapper.map(account, AccountUpdateDto.class);
        updateAccount.setName("핳핳!");

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

        AccountDeleteDto deleteAccount = modelMapper.map(account, AccountDeleteDto.class);

        String token = jwtTokenProvider.createToken(account.getEmail());

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(deleteAccount)))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(accountRepository.findByEmail(email).isPresent()).isFalse();
    }

    @Test
    public void delete_without_authentication() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        saveAccount(account);

        AccountDeleteDto deleteAccount = modelMapper.map(account, AccountDeleteDto.class);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteAccount)))
                .andDo(print())
                .andExpect(status().isForbidden());

        assertThat(accountRepository.findByEmail(email).isPresent()).isTrue();
    }

    private void saveAccount(Account account) {
        AccountRegistDto regist = modelMapper.map(account, AccountRegistDto.class);
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