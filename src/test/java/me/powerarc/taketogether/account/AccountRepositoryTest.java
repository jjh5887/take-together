package me.powerarc.taketogether.account;

import me.powerarc.taketogether.event.Event;
import me.powerarc.taketogether.event.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    EventRepository eventRepository;

    @Test
    public void crud() {
        // Given
        // Create
        Account account1 = createAccount("test1@test.com");
        Account account2 = createAccount("test2@test.com");
        account1 = accountRepository.save(account1);
        account2 = accountRepository.save(account2);

        Event event = createEvent();
        event.setHost(account1);
        Set<Account> participants = event.getParticipants();
        participants.add(account2);
        event = (Event) eventRepository.save(event);

        account1.getHostEvents().add(event);
        account2.getParticipantEvents().add(event);
        accountRepository.save(account1);
        accountRepository.save(account2);

        // Then
        Optional<Account> byId = accountRepository.findById(account1.getId());
        Account savedAccount = byId.orElseThrow(IllegalArgumentException::new);

        assertThat(savedAccount.getHostEvents().contains(event)).isTrue();
        assertThat(savedAccount).isEqualTo(account1);

        byId = accountRepository.findById(account2.getId());
        savedAccount = byId.orElseThrow(IllegalArgumentException::new);

        assertThat(savedAccount.getParticipantEvents().contains(event)).isTrue();
        assertThat(savedAccount).isEqualTo(account2);

        // Delete
        Set<Event> hostEvents = account1.getHostEvents();
        accountRepository.delete(account1);
        for (Event hostEvent : hostEvents) {
            assertThat(eventRepository.findById(hostEvent.getId())).isEmpty();
        }
        assertThat(accountRepository.findById(account1.getId())).isEmpty();
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

    private Account createAccount(String email) {
        Account account = Account.builder()
                .email(email)
                .password("pass")
                .hostEvents(new HashSet<>())
                .name("admin")
                .participantEvents(new HashSet<>())
                .build();
        return account;
    }
}