package me.powerarc.taketogether.event.response;

import lombok.Builder;
import lombok.Data;
import me.powerarc.taketogether.event.EventResource;

@Data
@Builder
public class EventSuccessResponse {
    private int status;
    private String message;
    private EventResource data;
}
