package me.powerarc.taketogether.account;

import lombok.*;
import me.powerarc.taketogether.event.Event;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;
    private String pass;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Event> hostEvents = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Event> participantEvents;

}
