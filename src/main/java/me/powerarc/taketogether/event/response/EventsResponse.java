package me.powerarc.taketogether.event.response;

import lombok.Builder;
import lombok.Data;
import me.powerarc.taketogether.event.Event;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@Data
@Builder
public class EventsResponse {
    private int status;
    private String message;
    private PagedModel<EntityModel<Event>> data;
}
