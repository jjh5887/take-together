package me.powerarc.taketogether.event;

import lombok.*;
import me.powerarc.taketogether.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;
    private String departure;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int price;
    private int totalNum;
    private int nowNum;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account host;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Account> participants;

}
