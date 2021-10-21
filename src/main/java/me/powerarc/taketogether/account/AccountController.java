package me.powerarc.taketogether.account;

import me.powerarc.taketogether.account.request.AccountDeleteRequest;
import me.powerarc.taketogether.account.request.AccountLoginRequest;
import me.powerarc.taketogether.account.request.AccountRegistRequest;
import me.powerarc.taketogether.account.request.AccountUpdateRequest;
import me.powerarc.taketogether.account.response.AccountFailResponse;
import me.powerarc.taketogether.account.response.AccountLoginResponse;
import me.powerarc.taketogether.account.response.AccountSuccessResponse;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AccountService accountService;

    @PostMapping("/regist")
    public ResponseEntity regist(@RequestBody @Valid AccountRegistRequest accountRegistRequest, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(AccountFailResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("fail")
                            .errors(errors).build());
        }

        accountService.createAccount(accountRegistRequest);

        return ResponseEntity.ok(AccountSuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success").build());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AccountLoginRequest accountLoginRequest, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(AccountFailResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("fail")
                            .errors(errors).build());
        }

        accountService.login(accountLoginRequest);

        return ResponseEntity.ok(AccountLoginResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .token(jwtTokenProvider.createToken(accountLoginRequest.getEmail())).build());
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid AccountUpdateRequest accountUpdateRequest, Errors errors, HttpServletRequest request) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(AccountFailResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("fail")
                            .errors(errors).build());
        }

        accountService.updateAccount(accountUpdateRequest, accountService.getAccount(jwtTokenProvider.getUserEmail(request)));

        return ResponseEntity.ok(AccountSuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success").build());
    }

    @DeleteMapping
    public ResponseEntity delete(@RequestBody @Valid AccountDeleteRequest accountDeleteRequest, Errors errors, HttpServletRequest request) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(AccountFailResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("fail")
                            .errors(errors).build());
        }

        accountService.delete(accountDeleteRequest, jwtTokenProvider.getUserEmail(request));

        return ResponseEntity.ok(AccountSuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success").build());
    }
}
