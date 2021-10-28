package me.powerarc.taketogether.event;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, String email, Link... links) {
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        add(linkTo(EventController.class).withRel("get-event"));
        add(linkTo(EventController.class).slash("name").withRel("get-events-name"));
        if (email != null) {
            add(linkTo(EventController.class).withRel("create-event"));
            if (event.getHost().getEmail().equals(email)) {
                add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
                add(linkTo(EventController.class).slash(event.getId()).withRel("delete-event"));
            }
        }
    }

    public EventResource(Event event, Link... links) {
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

    public EventResource(String email, Link... links) {
        super();
        add(links);
        add(linkTo(EventController.class).withRel("get-event"));
        add(linkTo(EventController.class).slash("name").withRel("get-events-name"));
        if (email != null)
            add(linkTo(EventController.class).withRel("create-event"));
    }
}
