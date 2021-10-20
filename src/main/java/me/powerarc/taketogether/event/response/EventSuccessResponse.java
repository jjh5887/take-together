package me.powerarc.taketogether.event.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventSuccessResponse {
    private int status;
    private String message;
}
