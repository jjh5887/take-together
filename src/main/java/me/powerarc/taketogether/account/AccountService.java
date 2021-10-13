package me.powerarc.taketogether.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getAccount(String email) throws Exception {
        return accountRepository.findByEmail(email).orElseThrow(Exception::new);
    }

    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

}
