package me.powerarc.taketogether.account.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountLoginResponse {
    private int status;
    private String message;

    private String token;
}
