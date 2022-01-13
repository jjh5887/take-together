package me.powerarc.taketogether.taxi;

import me.powerarc.taketogether.event.*;
import me.powerarc.taketogether.event.response.EventsResponse;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import me.powerarc.taketogether.taxi.request.TaxiCreateRequest;
import me.powerarc.taketogether.taxi.request.TaxiUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/taxi", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaxiController extends EventController<TaxiCreateRequest, TaxiUpdateRequest> {
    public TaxiController(TaxiService taxiService, JwtTokenProvider jwtTokenProvider,
                          EventValidator eventValidator, EventPagedResourceAssembler pagedResourceAssembler) {
        super(taxiService, jwtTokenProvider, eventValidator, pagedResourceAssembler);
    }

    @GetMapping("/kind/{kind}")
    public ResponseEntity getEventByKind(@PathVariable String kind,
                                         Pageable pageable,
                                         HttpServletRequest request) {
        Page<Event> events = ((TaxiService)eventService).getEventByKind(kind, pageable);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        PagedModel<EntityModel<Event>> eventResources =
                pagedResourceAssembler.toModel(events, this.getClass(), userEmail);
        return ResponseEntity.ok(EventsResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(eventResources).build());
    }
}
