package me.powerarc.taketogether.location;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class LocationResource extends CollectionModel<Location> {
    public LocationResource(List<Location> locations, String location, Link... links) {
        super(locations, links);
        add(linkTo(LocationController.class).slash(location).withSelfRel());
    }
}
