package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountService;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import me.powerarc.taketogether.event.response.EventFailResponse;
import me.powerarc.taketogether.event.response.EventResponse;
import me.powerarc.taketogether.event.response.EventSuccessResponse;
import me.powerarc.taketogether.event.response.EventsResponse;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    private final EventService eventService;
    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventCreateRequest eventCreateRequest, Errors errors, HttpServletRequest request) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").errors(errors).build());
        }
        eventValidator.validate(eventCreateRequest, errors);
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").errors(errors).build());

        Account account = accountService.getAccount(jwtTokenProvider.getUserEmail(request));
        Event event = eventService.createEvent(eventCreateRequest, account);

        if (event == null)
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").errors(errors).build());
        return ResponseEntity.ok(EventSuccessResponse.builder().status(HttpStatus.OK.value()).message("success").build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity getEvent(@PathVariable Long id) {
        Event event = eventService.getEvent(id);
        if (event == null) return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").build());
        return ResponseEntity.ok().body(EventResponse.builder().status(HttpStatus.OK.value()).message("success").event(event).build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity getEventByName(@PathVariable String name) {
        List<Event> events = eventService.getEvent(name);
        if (events == null)
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").build());
        return ResponseEntity.ok(EventsResponse.builder().status(HttpStatus.OK.value()).message("success").count(events.size()).events(events).build());
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity getEventByDestination(@PathVariable String destination) {
        List<Event> events = eventService.getEventByDestination(destination);
        if (events == null)
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").build());
        return ResponseEntity.ok(EventsResponse.builder().status(HttpStatus.OK.value()).message("success").count(events.size()).events(events).build());
    }

    @GetMapping("/departure/{departure}")
    public ResponseEntity getEventByDeparture(@PathVariable String departure) {
        List<Event> events = eventService.getEventByDeparture(departure);
        if (events == null)
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").build());
        return ResponseEntity.ok(EventsResponse.builder().status(HttpStatus.OK.value()).message("success").count(events.size()).events(events).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@RequestBody @Valid EventUpdateRequest eventUpdateRequest, Errors errors, @PathVariable Long id, HttpServletRequest request) throws Exception {
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").errors(errors).build());

        eventValidator.validate(eventUpdateRequest, errors);
        if (errors.hasErrors()) return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").errors(errors).build());

        Account account = accountService.getAccount(jwtTokenProvider.getUserEmail(request));
        if (!eventService.updateEvent(eventUpdateRequest, id, account))
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").errors(errors).build());

        return ResponseEntity.ok(EventSuccessResponse.builder().status(HttpStatus.OK.value()).message("success").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id, HttpServletRequest request) throws Exception {
        Account account = accountService.getAccount(jwtTokenProvider.getUserEmail(request));
        if (!eventService.deleteEvent(id, account))
            return ResponseEntity.badRequest().body(EventFailResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message("fail").build());
        return ResponseEntity.ok().body(EventSuccessResponse.builder().status(HttpStatus.OK.value()).message("success").build());
    }
}
