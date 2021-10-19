package me.powerarc.taketogether.account;

import me.powerarc.taketogether.account.dto.AccountDeleteDto;
import me.powerarc.taketogether.account.dto.AccountLoginDto;
import me.powerarc.taketogether.account.dto.AccountRegistDto;
import me.powerarc.taketogether.account.dto.AccountUpdateDto;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AccountService accountService;

    @PostMapping("/regist")
    public ResponseEntity regist(@RequestBody @Valid AccountRegistDto accountRegistDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        if (!accountService.createAccount(accountRegistDto)) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 존재하는 이메일 입니다."));
        }
        return ResponseEntity.ok(Map.of("message", "success"));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AccountLoginDto accountLoginDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        if (accountService.login(accountLoginDto)) {
            return ResponseEntity.ok(jwtTokenProvider.createToken(accountLoginDto.getEmail()));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "fail"));
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid AccountUpdateDto accountUpdateDto, Errors errors, HttpServletRequest request) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        if (accountService.updateAccount(accountUpdateDto, accountService.getAccount(jwtTokenProvider.getUserEmail(request)))) {
            return ResponseEntity.ok(Map.of("message", "success"));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "fail"));
    }

    @DeleteMapping
    public ResponseEntity delete(@RequestBody @Valid AccountDeleteDto accountDeleteDto, Errors errors, HttpServletRequest request) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        if (accountService.delete(accountDeleteDto, request, jwtTokenProvider)) {
            return ResponseEntity.ok(Map.of("message", "success"));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "fail"));
    }
}
