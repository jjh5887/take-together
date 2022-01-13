package me.powerarc.taketogether.event;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, String email, Class<?> clazz, Link... links) {
        super(event, links);
        add(linkTo(clazz).slash(event.getId()).withSelfRel());
        add(linkTo(clazz).withRel("get-event"));
        add(linkTo(clazz).slash("name").withRel("get-events-name"));
        if (email != null) {
            add(linkTo(clazz).withRel("create-event"));
            if (event.getHost().getEmail().equals(email)) {
                add(linkTo(clazz).slash(event.getId()).withRel("update-event"));
                add(linkTo(clazz).slash(event.getId()).withRel("delete-event"));
            }
        }
    }

    public EventResource(Event event, Class<?> clazz, Link... links) {
        super(event, links);
        add(linkTo(clazz).slash(event.getId()).withSelfRel());
    }

    public EventResource(String email, Class<?> clazz, Link... links) {
        super();
        add(links);
        add(linkTo(clazz).withRel("get-event"));
        add(linkTo(clazz).slash("name").withRel("get-events-name"));
        if (email != null)
            add(linkTo(clazz).withRel("create-event"));
    }
}
