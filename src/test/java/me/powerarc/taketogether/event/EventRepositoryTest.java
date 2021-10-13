package me.powerarc.taketogether.event;

import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
        Account host = createAccount("test1@test.com");
        Account account1 = createAccount("test2@test.com");
        Account account2 = createAccount("test3@test.com");
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

    @Test
    @Transactional
    public void findTimeBetween() {
        // Given
        List<Event> list = new ArrayList<>();
        Account account = createAccount("test@test.com");
        LocalDateTime start = LocalDateTime.of(2021, 10, 12, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 10, 12, 2, 2);
        accountRepository.save(account);
        for (int i = 0; i < 13; i++) {
            LocalDateTime startTime = start.plusHours(i).plusMinutes(i);
            LocalDateTime endTime = end.plusHours(i).plusMinutes(i);
            Event event = createEvent(startTime,
                    endTime,
                    "test" + i, i * 100);

            event.setHost(account);
            list.add(event);
        }
        eventRepository.saveAll(list);

        // Then
        start = LocalDateTime.of(2021, 10, 12, 1, 0);
        end = LocalDateTime.of(2021, 10, 12, 4, 5);
        List<Event> byDepartureTimeBetween = eventRepository.findByDepartureTimeBetween(start, end);

        assertThat(byDepartureTimeBetween.size()).isEqualTo(4);
        for (Event event : byDepartureTimeBetween) {
            assertThat(event.getDepartureTime().isBefore(end) &&
                    event.getDepartureTime().isAfter(start)).isTrue();
        }

        List<Event> byArrivalTimeBetween = eventRepository.findByArrivalTimeBetween(start, end);
        assertThat(byArrivalTimeBetween.size()).isEqualTo(3);
        for (Event event : byArrivalTimeBetween) {
            assertThat(event.getDepartureTime().isBefore(end) &&
                    event.getDepartureTime().isAfter(start)).isTrue();
        }

        List<Event> between = eventRepository.findByDepartureTimeBetweenAndArrivalTimeBetween(start, end,
                start.plusHours(1).plusMinutes(1), end.plusHours(1).plusMinutes(1));
        assertThat(between.size()).isEqualTo(4);
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

    private Event createEvent(LocalDateTime start, LocalDateTime end, String name, int price) {
        Event event = Event.builder()
                .name(name)
                .departure("from" + name)
                .destination("to" + name)
                .arrivalTime(end)
                .departureTime(start)
                .host(new Account())
                .participants(new HashSet<>())
                .nowNum(1)
                .price(price)
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