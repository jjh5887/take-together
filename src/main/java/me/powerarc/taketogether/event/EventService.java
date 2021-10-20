package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountService;
import me.powerarc.taketogether.account.request.AccountUpdateRequest;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public Event getEvent(Long id) {
        return eventRepository.findById(id).orElse(null);
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

    public LocalDateTime getLocalDateTime(String time) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return LocalDateTime.parse(time, format);
    }

    public boolean updateEvent(EventUpdateRequest eventUpdateRequest, Long id, Account account) {
        Event event = getEvent(id);
        if (!event.getHost().equals(account)) return false;

        modelMapper.map(eventUpdateRequest, event);
        eventRepository.save(event);

        return true;
    }

    public boolean deleteEvent(Long id, Account account) throws Exception {
        Event event = getEvent(id);
        if (!event.getHost().equals(account)) return false;

        try {
            eventRepository.delete(event);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Event createEvent(EventCreateRequest eventCreateRequest, Account account) throws Exception {
        Event event = modelMapper.map(eventCreateRequest, Event.class);
        event.setHost(account);
        event.addParticipants(account);
        Event savedEvent = eventRepository.save(event);

        AccountUpdateRequest updateAccount = modelMapper.map(account, AccountUpdateRequest.class);
        account.addEvent(event);
        accountService.updateAccount(updateAccount, account);
        return savedEvent;
    }
}
