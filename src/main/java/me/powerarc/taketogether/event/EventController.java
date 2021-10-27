package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import me.powerarc.taketogether.event.response.EventFailResponse;
import me.powerarc.taketogether.event.response.EventResponse;
import me.powerarc.taketogether.event.response.EventSuccessResponse;
import me.powerarc.taketogether.event.response.EventsResponse;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
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
public class EventController {
    private final EventService eventService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EventValidator eventValidator;
    private final EventPagedResourceAssembler pagedResourceAssembler;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventCreateRequest eventCreateRequest,
                                      Errors errors,
                                      HttpServletRequest request) throws Exception {
        if (eventValidator.validate(eventCreateRequest, errors)) return badRequest(errors);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        Event event = eventService.createEvent(eventCreateRequest, userEmail);
        return ResponseEntity.ok(EventResponse.builder()
                .status(HttpStatus.OK.value())
                .event(new EventResource(event, userEmail))
                .message("success").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Long id,
                                   HttpServletRequest request) {
        Event event = eventService.getEvent(id);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        return ResponseEntity.ok().body(EventResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .event(new EventResource(event, userEmail)).build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity getEventByName(@PathVariable String name,
                                         Pageable pageable,
                                         HttpServletRequest request) {
        Page<Event> events = eventService.getEvent(name, pageable);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        PagedModel<EntityModel<Event>> eventResources =
                pagedResourceAssembler.toModel(events, EventResource::new, userEmail);
        return ResponseEntity.ok(EventsResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .events(eventResources).build());
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity getEventByDestination(@PathVariable String destination,
                                                Pageable pageable,
                                                HttpServletRequest request) {
        Page<Event> events = eventService.getEventByDestination(destination, pageable);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        PagedModel<EntityModel<Event>> eventResources =
                pagedResourceAssembler.toModel(events, EventResource::new, userEmail);
        return ResponseEntity.ok(EventsResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .events(eventResources).build());
    }

    @GetMapping("/departure/{departure}")
    public ResponseEntity getEventByDeparture(@PathVariable String departure,
                                              Pageable pageable,
                                              HttpServletRequest request) {
        Page<Event> events = eventService.getEventByDeparture(departure, pageable);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        PagedModel<EntityModel<Event>> eventResources =
                pagedResourceAssembler.toModel(events, EventResource::new, userEmail);
        return ResponseEntity.ok(EventsResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .events(eventResources).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@RequestBody @Valid EventUpdateRequest eventUpdateRequest,
                                      Errors errors,
                                      @PathVariable Long id,
                                      HttpServletRequest request) {
        if (eventValidator.validate(eventUpdateRequest, errors)) return badRequest(errors);
        String userEmail = jwtTokenProvider.getUserEmail(request);
        Event event = eventService.updateEvent(eventUpdateRequest, id, userEmail);
        return ResponseEntity.ok(EventResponse.builder()
                .status(HttpStatus.OK.value())
                .event(new EventResource(event, userEmail))
                .message("success").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id,
                                      HttpServletRequest request) {
        String userEmail = jwtTokenProvider.getUserEmail(request);
        eventService.deleteEvent(id, userEmail);
        return ResponseEntity.ok().body(EventSuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .event(new EventResource(userEmail)).build());
    }

    private ResponseEntity<EventFailResponse> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EventFailResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("fail")
                .errors(errors).build());
    }
}
