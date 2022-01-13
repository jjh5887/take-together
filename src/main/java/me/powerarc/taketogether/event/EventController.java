package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import me.powerarc.taketogether.event.response.EventResponse;
import me.powerarc.taketogether.event.response.EventSuccessResponse;
import me.powerarc.taketogether.event.response.EventsResponse;
import me.powerarc.taketogether.exception.ExceptionResponse;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController<C extends EventCreateRequest, U extends EventUpdateRequest> {
    protected final EventService eventService;
    protected final JwtTokenProvider jwtTokenProvider;
    protected final EventValidator eventValidator;
    protected final EventPagedResourceAssembler pagedResourceAssembler;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid C eventCreateRequest,
                                      Errors errors,
                                      HttpServletRequest request) {
        if (eventValidator.validate(eventCreateRequest, errors)) return badRequest(errors);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        Event event = eventService.createEvent(eventCreateRequest, userEmail);
        EventResource profile = new EventResource(event, userEmail, this.getClass(), Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.ok(EventResponse.builder()
                .status(HttpStatus.OK.value())
                .data(profile)
                .message("success").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Long id,
                                   HttpServletRequest request) throws Throwable {
        Event event = eventService.getEvent(id);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        return ResponseEntity.ok().body(EventResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(new EventResource(event, userEmail, this.getClass(), Link.of("/docs/index.html#resources-event").withRel("profile"))).build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity getEventByName(@PathVariable String name,
                                         Pageable pageable,
                                         HttpServletRequest request) {
        Page events = eventService.getEvent(name, pageable);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        PagedModel eventResources =
                pagedResourceAssembler.toModel(events, this.getClass(), userEmail);
        return ResponseEntity.ok(EventsResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(eventResources).build());
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity getEventByDestination(@PathVariable String destination,
                                                Pageable pageable,
                                                HttpServletRequest request) {
        Page events = eventService.getEventByDestination(destination, pageable);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        PagedModel eventResources =
                pagedResourceAssembler.toModel(events, this.getClass(), userEmail);
        return ResponseEntity.ok(EventsResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(eventResources).build());
    }

    @GetMapping("/departure/{departure}")
    public ResponseEntity getEventByDeparture(@PathVariable String departure,
                                              Pageable pageable,
                                              HttpServletRequest request) {
        Page events = eventService.getEventByDeparture(departure, pageable);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        PagedModel eventResources =
                pagedResourceAssembler.toModel(events, this.getClass(), userEmail);
        return ResponseEntity.ok(EventsResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(eventResources).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@RequestBody @Valid U eventUpdateRequest,
                                      Errors errors,
                                      @PathVariable Long id,
                                      HttpServletRequest request) throws Throwable {
        if (eventValidator.validate(eventUpdateRequest, errors)) return badRequest(errors);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        Event event = eventService.updateEvent(eventUpdateRequest, id, userEmail);
        return ResponseEntity.ok(EventResponse.builder()
                .status(HttpStatus.OK.value())
                .data(new EventResource(event, userEmail, this.getClass(), Link.of("/docs/index.html#resources-events-update").withRel("profile")))
                .message("success").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id,
                                      HttpServletRequest request) throws Throwable {
        String userEmail = jwtTokenProvider.getUserEmail(request);
        eventService.deleteEvent(id, userEmail);
        return ResponseEntity.ok().body(EventSuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(new EventResource(userEmail, this.getClass(), Link.of("/docs/index.html#resources-events-delete").withRel("profile"))).build());
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errors.getAllErrors().get(0).getDefaultMessage()).build());
    }
}
