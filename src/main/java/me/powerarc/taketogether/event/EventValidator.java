package me.powerarc.taketogether.event;

import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public boolean validate(EventUpdateRequest eventUpdateRequest, Errors errors) {
        if (errors.hasErrors()) {
            System.out.println(errors.getAllErrors().get(0).getObjectName());
            return true;
        }
        if (eventUpdateRequest.getArrivalTime().isBefore(eventUpdateRequest.getDepartureTime()))
            errors.reject("wrongArrivalTime", "도착시간이 출발시간보다 빠릅니다.");

        if (eventUpdateRequest.getParticipants_id().size() > eventUpdateRequest.getTotalNum())
            errors.reject("wrongParticipants", "참여인원이 초과되었습니다.");

        return errors.hasErrors();
    }

    public boolean validate(EventCreateRequest eventCreateRequest, Errors errors) {
        if (errors.hasErrors()) {
            System.out.println(errors.getAllErrors());
            return true;
        }
        if (eventCreateRequest.getArrivalTime().isBefore(eventCreateRequest.getDepartureTime()))
            errors.reject("wrongArrivalTime", "도착시간이 출발시간보다 빠릅니다.");

        return errors.hasErrors();
    }
}
