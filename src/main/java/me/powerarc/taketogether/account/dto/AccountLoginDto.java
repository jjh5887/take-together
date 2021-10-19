package me.powerarc.taketogether.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLoginDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
