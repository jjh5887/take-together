package me.powerarc.taketogether.account.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountSuccessResponse {
    private int status;
    private String message;
}
