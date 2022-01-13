package me.powerarc.taketogether.route;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class RouteResource extends CollectionModel<Route> {

    public RouteResource(List<Route> routes, Link... links) {
        super(routes, links);
        add(linkTo(RouteController.class).withSelfRel());
    }
}
