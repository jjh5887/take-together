package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountService;
import me.powerarc.taketogether.account.request.AccountUpdateRequest;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import me.powerarc.taketogether.exception.WebException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public Event createEvent(EventCreateRequest eventCreateRequest, String email) throws Exception {
        if (eventRepository.existsByName(eventCreateRequest.getName()))
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "중복된 이름입니다.");

        Account account = accountService.getAccount(email);

        Event event = modelMapper.map(eventCreateRequest, Event.class);
        event.setHost(account);
        event.addParticipants(account);
        Event savedEvent = eventRepository.save(event);

        AccountUpdateRequest updateAccount = modelMapper.map(account, AccountUpdateRequest.class);
        account.addEvent(event);
        accountService.updateAccount(updateAccount, account);

        return savedEvent;
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new WebException(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 이벤트입니다."));
    }

    public List<Event> getEvent(String name) {
        return eventRepository.findByNameContains(name);
    }

    public List<Event> getEventByDeparture(String departure) {
        return eventRepository.findByDepartureContains(departure);
    }

    public List<Event> getEventByDestination(String destination) {
        return eventRepository.findByDestinationContains(destination);
    }

    public List<Event> getEventByDepartureTime(String startTime, String endTime) {
        LocalDateTime start = getLocalDateTime(startTime);
        LocalDateTime end = getLocalDateTime(endTime);
        return eventRepository.findByDepartureTimeBetween(start, end);
    }

    public List<Event> getEventByArrivalTime(String startTime, String endTime) {
        LocalDateTime start = getLocalDateTime(startTime);
        LocalDateTime end = getLocalDateTime(endTime);
        return eventRepository.findByArrivalTimeBetween(start, end);
    }

    public List<Event> getEvent(String startTimeA, String endTimeA, String startTimeB, String endTimeB) {
        LocalDateTime startA = getLocalDateTime(startTimeA);
        LocalDateTime startB = getLocalDateTime(startTimeB);
        LocalDateTime endA = getLocalDateTime(endTimeA);
        LocalDateTime endB = getLocalDateTime(endTimeB);
        return eventRepository.findByDepartureTimeBetweenAndArrivalTimeBetween(startA, endA, startB, endB);
    }

    public void updateEvent(EventUpdateRequest eventUpdateRequest, Long id, String email) {
        Event event = getEvent(id);
        Account account = accountService.getAccount(email);

        if (!event.getHost().equals(account))
            throw new WebException(HttpStatus.FORBIDDEN.value(), "권한이 없습니다.");

        modelMapper.map(eventUpdateRequest, event);

        event.setHost(accountService.getAccount(eventUpdateRequest.getHost_id()));
        Set<Account> accountSet = new HashSet<>();
        for (Long participant_id : eventUpdateRequest.getParticipants_id()) {
            accountSet.add(accountService.getAccount(participant_id));
        }
        event.setParticipants(accountSet);

        eventRepository.save(event);
    }

    public void deleteEvent(Long id, String email) throws Exception {
        Account account = accountService.getAccount(email);

        Event event = getEvent(id);
        if (!event.getHost().equals(account))
            throw new WebException(HttpStatus.FORBIDDEN.value(), "권한이 없습니다.");

        eventRepository.delete(event);
    }

    public LocalDateTime getLocalDateTime(String time) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return LocalDateTime.parse(time, format);
    }
}
