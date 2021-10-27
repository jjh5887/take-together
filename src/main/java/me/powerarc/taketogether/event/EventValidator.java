package me.powerarc.taketogether.event;

import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public boolean validate(EventUpdateRequest eventUpdateRequest, Errors errors) {
        if (errors.hasErrors()) return true;
        if (eventUpdateRequest.getArrivalTime().isBefore(eventUpdateRequest.getDepartureTime()))
            errors.reject("wrongArrivalTime", "Arrival time is earlier than departure time");

        if (eventUpdateRequest.getParticipants_id().size() > eventUpdateRequest.getTotalNum())
            errors.reject("wrongParticipants", "The number of participants has exceeded");

        return errors.hasErrors();
    }

    public boolean validate(EventCreateRequest eventCreateRequest, Errors errors) {
        if (errors.hasErrors()) return true;
        if (eventCreateRequest.getArrivalTime().isBefore(eventCreateRequest.getDepartureTime()))
            errors.reject("wrongArrivalTime", "Arrival time is earlier than departure time");

        return errors.hasErrors();
    }
}
