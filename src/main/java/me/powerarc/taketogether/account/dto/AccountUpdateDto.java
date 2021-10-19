package me.powerarc.taketogether.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {
    @NotEmpty
    private String password;
    private String name;
    private String email;
    private String newPassword;
}
