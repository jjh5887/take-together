package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountService;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import me.powerarc.taketogether.exception.WebException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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

        account.addEvent(event);
        accountService.saveAccount(account);

        return savedEvent;
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 이벤트입니다."));
    }

    public Page<Event> getEvent(String name, Pageable pageable) {
        return eventRepository.findByNameContains(name, pageable);
    }

    public Page<Event> getEventByDeparture(String departure, Pageable pageable) {
        return eventRepository.findByDepartureContains(departure, pageable);
    }

    public Page<Event> getEventByDestination(String destination, Pageable pageable) {
        return eventRepository.findByDestinationContains(destination, pageable);
    }

    public Page<Event> getEventByDepartureTime(String startTime, String endTime, Pageable pageable) {
        LocalDateTime start = getLocalDateTime(startTime);
        LocalDateTime end = getLocalDateTime(endTime);
        return eventRepository.findByDepartureTimeBetween(start, end, pageable);
    }

    public Page<Event> getEventByArrivalTime(String startTime, String endTime, Pageable pageable) {
        LocalDateTime start = getLocalDateTime(startTime);
        LocalDateTime end = getLocalDateTime(endTime);
        return eventRepository.findByArrivalTimeBetween(start, end, pageable);
    }

    public Page<Event> getEvent(String startTimeA, String endTimeA, String startTimeB, String endTimeB, Pageable pageable) {
        LocalDateTime startA = getLocalDateTime(startTimeA);
        LocalDateTime startB = getLocalDateTime(startTimeB);
        LocalDateTime endA = getLocalDateTime(endTimeA);
        LocalDateTime endB = getLocalDateTime(endTimeB);
        return eventRepository.findByDepartureTimeBetweenAndArrivalTimeBetween(startA, endA, startB, endB, pageable);
    }

    public Event updateEvent(EventUpdateRequest eventUpdateRequest, Long id, String email) {
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

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id, String email) {
        Account account = accountService.getAccount(email);

        Event event = getEvent(id);
        if (!event.getHost().equals(account))
            throw new WebException(HttpStatus.FORBIDDEN.value(), "권한이 없습니다.");

        account.getHostEvents().remove(event);
        account.getParticipantEvents().remove(event);
        accountService.saveAccount(account);

        eventRepository.delete(event);
    }

    public LocalDateTime getLocalDateTime(String time) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return LocalDateTime.parse(time, format);
    }
}
