package me.powerarc.taketogether.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {
    private String email;
    private String password;
}
