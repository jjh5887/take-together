package me.powerarc.taketogether.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getArrivalTime().isBefore(eventDto.getDepartureTime()))
            errors.reject("wrongArrivalTime", "Arrival time is earlier than departure time");

        if (eventDto.getParticipants().size() > eventDto.getTotalNum())
            errors.reject("wrongParticipants", "The number of participants has exceeded");
    }
}
