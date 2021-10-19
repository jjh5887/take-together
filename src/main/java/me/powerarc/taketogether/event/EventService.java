package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountService;
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

    public Event updateEvent(EventDto eventDto, Long id) {
        Event event = getEvent(id);
        modelMapper.map(eventDto, event);
        return eventRepository.save(event);
    }

    public boolean deleteEvent(Long id) throws Exception {
        try {
            eventRepository.delete(getEvent(id));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Event createEvent(EventDto eventDto, Account account) {
        Event event = modelMapper.map(eventDto, Event.class);
        event.setHost(account);
        event.addParticipants(account);
        Event savedEvent = eventRepository.save(event);

        account.addEvent(event);
//        accountService.updateAccount(account);
        return savedEvent;
    }
}
