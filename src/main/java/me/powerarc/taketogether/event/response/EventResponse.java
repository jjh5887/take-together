package me.powerarc.taketogether.event.response;

import lombok.Builder;
import lombok.Data;
import me.powerarc.taketogether.event.EventResource;

@Data
@Builder
public class EventResponse {
    private int status;
    private String message;

    private EventResource data;
}
