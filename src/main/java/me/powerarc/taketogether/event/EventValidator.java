package me.powerarc.taketogether.event;

import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public void validate(EventUpdateRequest eventUpdateRequest, Errors errors) {
        if (eventUpdateRequest.getArrivalTime().isBefore(eventUpdateRequest.getDepartureTime()))
            errors.reject("wrongArrivalTime", "Arrival time is earlier than departure time");

        if (eventUpdateRequest.getParticipants().size() > eventUpdateRequest.getTotalNum())
            errors.reject("wrongParticipants", "The number of participants has exceeded");
    }

    public void validate(EventCreateRequest eventCreateRequest, Errors errors) {
        if (eventCreateRequest.getArrivalTime().isBefore(eventCreateRequest.getDepartureTime()))
            errors.reject("wrongArrivalTime", "Arrival time is earlier than departure time");
    }
}
