package me.powerarc.taketogether.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.Errors;

@Builder
@Data
public class AccountFailResponse {
    private int status;
    private String message;

    private Errors errors;
}
