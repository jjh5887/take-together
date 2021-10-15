package me.powerarc.taketogether.event;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.serializer.AccountSerializer;
import me.powerarc.taketogether.account.serializer.AccountSetSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    @JoinColumn(updatable = false)
    @JsonSerialize(using = AccountSerializer.class)
    private Account host;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    @JsonSerialize(using = AccountSetSerializer.class)
    @JoinColumn(updatable = false)
    private Set<Account> participants = new HashSet<>();

    public void addParticipants(Account account) {
        participants.add(account);
        nowNum = participants.size();
    }

}
