package me.powerarc.taketogether.event;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.account.AccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    private final EventService eventService;
    private final AccountService accountService;
    private final EventValidator eventValidator;

    @PostMapping("/{email}")
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @PathVariable String email) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) return ResponseEntity.badRequest().body(errors);

        Event events = eventService.createEvent(eventDto, accountService.getAccount(email));
        if (events == null) return ResponseEntity.badRequest().body("error");
        return ResponseEntity.ok(events);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity getEvent(@PathVariable Long id) {
        Event event = eventService.getEvent(id);
        if (event == null) return ResponseEntity.badRequest().body("error");
        return ResponseEntity.ok().body(event);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity getEventByName(@PathVariable String name) {
        List<Event> event = eventService.getEvent(name);
        if (event == null) return ResponseEntity.badRequest().body("error");
        return ResponseEntity.ok(event);
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity getEventByDestination(@PathVariable String destination) {
        List<Event> events = eventService.getEventByDestination(destination);
        if (events == null) return ResponseEntity.badRequest().body("error");
        return ResponseEntity.ok().body(events);
    }

    @GetMapping("/departure/{departure}")
    public ResponseEntity getEventByDeparture(@PathVariable String departure) {
        List<Event> events = eventService.getEventByDeparture(departure);
        if (events == null) return ResponseEntity.badRequest().body("error");
        return ResponseEntity.ok().body(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @PathVariable Long id) throws Exception {
        if (errors.hasErrors()) return ResponseEntity.badRequest().body(errors);
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) return ResponseEntity.badRequest().body(errors);

        Event event = eventService.updateEvent(eventDto, id);
        if (event == null) return ResponseEntity.badRequest().body("error");
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id) throws Exception {
        if (!eventService.deleteEvent(id))
            return ResponseEntity.badRequest().body("error");
        return ResponseEntity.ok().body("ok");
    }
}
