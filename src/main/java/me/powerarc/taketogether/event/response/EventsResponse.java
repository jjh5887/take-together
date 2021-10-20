package me.powerarc.taketogether.event.response;

import lombok.Builder;
import lombok.Data;
import me.powerarc.taketogether.event.Event;

import java.util.List;

@Data
@Builder
public class EventsResponse {
    private int status;
    private String message;

    private int count;
    private List<Event> events;
}
