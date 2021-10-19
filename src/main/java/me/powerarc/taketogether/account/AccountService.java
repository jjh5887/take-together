package me.powerarc.taketogether.account;

import lombok.SneakyThrows;
import me.powerarc.taketogether.account.dto.AccountDeleteDto;
import me.powerarc.taketogether.account.dto.AccountLoginDto;
import me.powerarc.taketogether.account.dto.AccountRegistDto;
import me.powerarc.taketogether.account.dto.AccountUpdateDto;
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

    public boolean updateAccount(AccountUpdateDto accountUpdateDto, Account account) throws Exception {
        if (passwordEncoder.matches(accountUpdateDto.getPassword(), account.getPassword())) {
            modelMapper.map(accountUpdateDto, account);
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

    public boolean createAccount(AccountRegistDto accountRegistDto) {
        if (!accountRepository.existsByEmail(accountRegistDto.getEmail())) {
            Account account = modelMapper.map(accountRegistDto, Account.class);
            account.encodePassword(passwordEncoder);
            account.addRole(AccountRole.USER);
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    public boolean login(AccountLoginDto accountLoginDto) {
        Account byEmail = accountRepository.findByEmail(accountLoginDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(accountLoginDto.getEmail()));
        return passwordEncoder.matches(accountLoginDto.getPassword(), byEmail.getPassword());
    }

    public boolean delete(AccountDeleteDto accountDeleteDto, HttpServletRequest request, JwtTokenProvider jwtTokenProvider) throws Exception {
        String token = this.jwtTokenProvider.resolveToken(request);
        String userEmail = this.jwtTokenProvider.getUserEmail(token);
        Account account = getAccount(userEmail);
        if (passwordEncoder.matches(accountDeleteDto.getPassword(), account.getPassword())) {
            accountRepository.delete(account);
            return true;
        }
        return false;
    }
}
