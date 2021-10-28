package me.powerarc.taketogether.event;

import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.util.UriComponents;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventPagedResourceAssembler extends PagedResourcesAssembler<Event> {
    public EventPagedResourceAssembler(HateoasPageableHandlerMethodArgumentResolver resolver, UriComponents baseUri) {
        super(resolver, baseUri);
    }

    public <R extends RepresentationModel<?>> PagedModel<EntityModel<Event>> toModel(Page<Event> page, RepresentationModelAssembler<Event, R> assembler, String email) {
        PagedModel<R> pagedModel = super.toModel(page, assembler);
        pagedModel.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        if (email != null) {
            pagedModel.add(linkTo(EventController.class).withRel("create-event"));
        }
        return (PagedModel<EntityModel<Event>>) pagedModel;
    }
}
