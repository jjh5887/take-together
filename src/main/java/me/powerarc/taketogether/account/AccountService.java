package me.powerarc.taketogether.account;

import lombok.SneakyThrows;
import me.powerarc.taketogether.account.request.AccountDeleteRequest;
import me.powerarc.taketogether.account.request.AccountLoginRequest;
import me.powerarc.taketogether.account.request.AccountRegistRequest;
import me.powerarc.taketogether.account.request.AccountUpdateRequest;
import me.powerarc.taketogether.exception.WebException;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void createAccount(AccountRegistRequest accountRegistRequest) {
        if (accountRepository.existsByEmail(accountRegistRequest.getEmail()))
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 계정입니다.");

        Account account = modelMapper.map(accountRegistRequest, Account.class);
        account.encodePassword(passwordEncoder);
        account.addRole(AccountRole.USER);
        accountRepository.save(account);
    }


    public Account getAccount(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 계정입니다."));
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 계정입니다."));
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    public void updateAccount(AccountUpdateRequest accountUpdateRequest, Account account) throws Exception {
        if (!passwordEncoder.matches(accountUpdateRequest.getPassword(), account.getPassword()))
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
        modelMapper.map(accountUpdateRequest, account);
        accountRepository.save(account);
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다."));
    }

    public void login(AccountLoginRequest accountLoginRequest) {
        Account account = getAccount(accountLoginRequest.getEmail());

        if (!passwordEncoder.matches(accountLoginRequest.getPassword(), account.getPassword()))
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
    }

    public void delete(AccountDeleteRequest accountDeleteRequest, String email) {
        Account account = getAccount(email);
        if (!passwordEncoder.matches(accountDeleteRequest.getPassword(), account.getPassword()))
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
        accountRepository.delete(account);
    }
}
