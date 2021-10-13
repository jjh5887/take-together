package me.powerarc.taketogether.event;

import lombok.Builder;
import lombok.Data;
import me.powerarc.taketogether.account.Account;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class EventDto {
    private String name;
    private String departure;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int price;
    private int totalNum;
    private int nowNum;

    private Account host;
    private Set<Account> participants;
}
