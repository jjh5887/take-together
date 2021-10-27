package me.powerarc.taketogether.event;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, String email) {
        super(event);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        add(linkTo(EventController.class).withRel("query-event-id"));
        add(linkTo(EventController.class).slash("name").withRel("query-events-name"));
        if (email != null) {
            add(linkTo(EventController.class).withRel("create-event"));
            if (event.getHost().getEmail().equals(email)) {
                add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
                add(linkTo(EventController.class).slash(event.getId()).withRel("delete-event"));
            }
        }
    }

    public EventResource(Event event) {
        super(event);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

    public EventResource(String email) {
        super();
        add(linkTo(EventController.class).slash("id").withRel("query-event-id"));
        add(linkTo(EventController.class).slash("name").withRel("query-events-name"));
        if (email != null)
            add(linkTo(EventController.class).withRel("create-event"));
    }
}
