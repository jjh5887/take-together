package me.powerarc.taketogether.account;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;
    private String password;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    private Set<Event> hostEvents = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    @JoinColumn(updatable = false)
    private Set<Event> participantEvents = new HashSet<>();

    public void addEvent(Event event) {
        hostEvents.add(event);
        participantEvents.add(event);
    }

}
