package me.powerarc.taketogether.event;

import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void crud() {
        // Given
        Account host = creatAccount("test1@test.com");
        Account account1 = creatAccount("test2@test.com");
        Account account2 = creatAccount("test3@test.com");
        host = accountRepository.save(host);
        account1 = accountRepository.save(account1);
        account2 = accountRepository.save(account2);

        Event event = createEvent();
        event.setHost(host);

        Set<Account> participants = event.getParticipants();
        participants.add(account1);
        participants.add(account2);
        event.setParticipants(participants);
        Event save = eventRepository.save(event);

        // Then
        Optional<Event> byId = eventRepository.findById(save.getId());
        Event savedEvent = byId.orElseThrow(IllegalArgumentException::new);
        assertThat(savedEvent).isEqualTo(event);

        Set<Account> savedEventParticipants = savedEvent.getParticipants();
        savedEventParticipants.forEach(participant -> {
            assertThat(participants.contains(participant)).isTrue();
        });

        Account savedEventHost = savedEvent.getHost();
        assertThat(savedEventHost).isEqualTo(host);

        // Delete
        eventRepository.delete(event);
        assertThat(eventRepository.findById(event.getId())).isEmpty();

    }

    private Event createEvent() {
        Event event = Event.builder()
                .name("take-together")
                .departure("incheon")
                .destination("seoul")
                .arrivalTime(LocalDateTime.of(2021, 10, 12, 8, 0))
                .departureTime(LocalDateTime.of(2021, 10, 12, 8, 45))
                .host(new Account())
                .participants(new HashSet<>())
                .nowNum(1)
                .price(5000)
                .totalNum(4)
                .build();
        return event;
    }

    private Account creatAccount(String email) {
        Account account = Account.builder()
                .email(email)
                .pass("pass")
                .hostEvents(new HashSet<>())
                .name("admin")
                .participantEvents(new HashSet<>())
                .build();
        return account;
    }
}