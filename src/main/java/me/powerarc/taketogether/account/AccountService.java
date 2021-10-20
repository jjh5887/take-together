package me.powerarc.taketogether.account;

import lombok.SneakyThrows;
import me.powerarc.taketogether.account.request.AccountDeleteRequest;
import me.powerarc.taketogether.account.request.AccountLoginRequest;
import me.powerarc.taketogether.account.request.AccountRegistRequest;
import me.powerarc.taketogether.account.request.AccountUpdateRequest;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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

    public AccountService() {
    }

    public Account getAccount(String email) throws Exception {
        return accountRepository.findByEmail(email).orElseThrow(Exception::new);
    }

    public boolean updateAccount(AccountUpdateRequest accountUpdateRequest, Account account) throws Exception {
        if (passwordEncoder.matches(accountUpdateRequest.getPassword(), account.getPassword())) {
            modelMapper.map(accountUpdateRequest, account);
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public boolean createAccount(AccountRegistRequest accountRegistRequest) {
        if (!accountRepository.existsByEmail(accountRegistRequest.getEmail())) {
            Account account = modelMapper.map(accountRegistRequest, Account.class);
            account.encodePassword(passwordEncoder);
            account.addRole(AccountRole.USER);
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    public boolean login(AccountLoginRequest accountLoginRequest) {
        Account byEmail = accountRepository.findByEmail(accountLoginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(accountLoginRequest.getEmail()));
        return passwordEncoder.matches(accountLoginRequest.getPassword(), byEmail.getPassword());
    }

    public boolean delete(AccountDeleteRequest accountDeleteRequest, HttpServletRequest request, JwtTokenProvider jwtTokenProvider) throws Exception {
        String token = this.jwtTokenProvider.resolveToken(request);
        String userEmail = this.jwtTokenProvider.getUserEmail(token);
        Account account = getAccount(userEmail);
        if (passwordEncoder.matches(accountDeleteRequest.getPassword(), account.getPassword())) {
            accountRepository.delete(account);
            return true;
        }
        return false;
    }
}
