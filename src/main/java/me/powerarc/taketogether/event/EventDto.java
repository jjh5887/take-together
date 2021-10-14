package me.powerarc.taketogether.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.powerarc.taketogether.account.Account;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    @NotNull
    private String name;
    @NotNull
    private String departure;
    @NotNull
    private String destination;
    @NotNull
    private LocalDateTime departureTime;
    @NotNull
    private LocalDateTime arrivalTime;
    @Min(0)
    private int price;
    @Min(2)
    @Max(4)
    private int totalNum;

    @NotNull
    private Account host;
    @NotNull
    private Set<Account> participants;

    public void addParticipants(Account account) {
        participants.add(account);
    }
}
