package me.powerarc.taketogether.event.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.Errors;

@Data
@Builder
public class EventFailResponse {
    private int status;
    private String message;

    private Errors errors;
}
