package me.powerarc.taketogether.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountRepository;
import me.powerarc.taketogether.account.AccountRole;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void getEventById() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeEvent(account, "test", "Incheon", "Seoul");
        account.addEvent(event);
        accountRepository.save(account);

        // Then
        mockMvc.perform(get("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.event.id").value(event.getId()))
                .andExpect(jsonPath("$.event.departure").value(event.getDeparture()))
                .andExpect(jsonPath("$.event.arrivalTime").value(event.getArrivalTime().toString()))
                .andExpect(jsonPath("$.event.host.id").value(account.getId().toString()))
                .andExpect(jsonPath("$.event.participants[0].id").value(account.getId().toString()))
        ;
    }

    @Test
    public void getEventByWrongId() throws Exception {
        // Then
        mockMvc.perform(get("/event/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("message", is("존재하지 않는 이벤트입니다.")))
        ;
    }

    @Test
    public void getEventByName() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeEvent(account, "test", "Incheon", "Seoul");
        account.addEvent(event);
        accountRepository.save(account);

        // Then
        mockMvc.perform(get("/event/name/" + event.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.events.content[0].id").value(event.getId()))
                .andExpect(jsonPath("$.events.content[0].name").value(event.getName()))
                .andExpect(jsonPath("$.events.content[0].departure").value(event.getDeparture()))
                .andExpect(jsonPath("$.events.content[0].arrivalTime").value(event.getArrivalTime().toString()))
                .andExpect(jsonPath("$.events.content[0].host.id").value(account.getId().toString()))
                .andExpect(jsonPath("$.events.content[0].participants[0].id").value(account.getId().toString()))
                .andExpect(jsonPath("$.events.content[1]").doesNotExist())
        ;
    }

    @Test
    public void getEventByWrongName() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeEvent(account, "test", "Incheon", "Seoul");
        account.addEvent(event);
        accountRepository.save(account);

        // Then
        mockMvc.perform(get("/event/name/" + event.getName() + "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.events.content").isEmpty())
        ;
    }

    @Test
    public void getEventByDeparture() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        String departure = "Incheon";
        Event event = makeEvent(account, "test", departure, "Seoul");
        Event event1 = makeEvent(account, "test1", departure + " Gaeyang", "Seoul");
        Event event2 = makeEvent(account, "test2", "Gaeyang", "Seoul");

        account.addEvent(event);
        account.addEvent(event1);
        account.addEvent(event2);
        accountRepository.save(account);


        // Then
        mockMvc.perform(get("/event/departure/" + departure)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.events.content[*].departure", hasItem(departure)))
                .andExpect(jsonPath("$.events.content", hasSize(2)))
        ;
    }

    @Test
    public void getEventByDestination() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        String destination = "Seoul";
        Event event = makeEvent(account, "test", "Incheon", destination);
        Event event1 = makeEvent(account, "test1", "Incheon", destination + " station");
        Event event2 = makeEvent(account, "test2", "Incheon", "station");

        account.addEvent(event);
        account.addEvent(event1);
        account.addEvent(event2);
        accountRepository.save(account);

        // Then
        mockMvc.perform(get("/event/destination/" + destination)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.events.content[*].destination", hasItem(destination)))
                .andExpect(jsonPath("$.events.content", hasSize(2)))
        ;
    }

    @Test
    public void createEvent_no_authentication() throws Exception {
        // Given
        String email = "test@test.com";
        makeAccount(email);

        String name = "test";
        String departure = "Incheon";
        String destination = "Seoul";
        EventCreateRequest eventCreateRequest = makeEventCreatRequest(name, departure, destination);

        // Then
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventCreateRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    public void createEvent() throws Exception {
        // Given
        String email = "test@test.com";
        makeAccount(email);

        String name = "test";
        String departure = "Incheon";
        String destination = "Seoul";
        EventCreateRequest eventCreateRequest = makeEventCreatRequest(name, departure, destination);

        String token = jwtTokenProvider.createToken(email);

        // Then
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventCreateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.event.links[0].rel", is("self")))
        ;

//        List<Event> byNameContains = eventRepository.findByNameContains(eventCreateRequest.getName());
//        assertThat(byNameContains.size()).isEqualTo(1);
//        assertThat(byNameContains.get(0).getName()).isEqualTo(eventCreateRequest.getName());
//        assertThat(byNameContains.get(0).getHost().getEmail()).isEqualTo(email);
    }

    @Test
    public void createEvent_duplicate_name() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        String token = jwtTokenProvider.createToken(email);

        String name = "test";
        String departure = "Incheon";
        String destination = "Seoul";
        makeEvent(account, name, departure, destination);

        EventCreateRequest eventCreateRequest = makeEventCreatRequest(name, departure, destination);

        // Then
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventCreateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("중복된 이름입니다.")))
        ;
    }

    @Test
    public void updateEvent_no_authentication() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);

        String name = "before";
        Event event = makeEvent(account, name, name + " dep", name + " dest");

        String newEmail = "newTest@test.com";
        Account newAccount = makeAccount(newEmail);

        String newName = "after";
        EventUpdateRequest eventUpdateRequest = makeEventDto(newAccount.getId(), newName, newName + " dep", newName + " dest");

        // Then
        mockMvc.perform(put("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;

        // Given
        Account byIdAccount = accountRepository.findById(account.getId()).get();
        Account byIdNewAccount = accountRepository.findById(newAccount.getId()).get();

        // Then
        assertThat(byIdAccount).isEqualTo(account);
        assertThat(byIdNewAccount).isEqualTo(newAccount);
    }

    @Test
    public void updateEvent() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);

        String name = "before";
        Event event = makeEvent(account, name, name + " dep", name + " dest");

        String newEmail = "newTest@test.com";
        Account newAccount = makeAccount(newEmail);
        String newName = "after";
        EventUpdateRequest eventUpdateRequest = makeEventDto(newAccount.getId(), newName, newName + " dep", newName + " dest");

        String token = jwtTokenProvider.createToken(email);

        // Then
        mockMvc.perform(put("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
        ;

        // Given
        Event byId = eventRepository.findById(event.getId()).get();

        // Then
        assertThat(byId.getHost().getEmail()).isEqualTo(newAccount.getEmail());
        assertThat(byId.getHost().getId()).isEqualTo(newAccount.getId());
        assertThat(byId.getId()).isEqualTo(event.getId());
        assertThat(byId.getName()).isEqualTo(eventUpdateRequest.getName());
        assertThat(byId.getDeparture()).isEqualTo(eventUpdateRequest.getDeparture());
        assertThat(byId.getDepartureTime()).isEqualTo(eventUpdateRequest.getDepartureTime());


        // Given
        Account byIdAccount = accountRepository.findById(account.getId()).get();
        Account byIdNewAccount = accountRepository.findById(newAccount.getId()).get();

        // Then
        assertThat(byIdAccount).isEqualTo(account);
        assertThat(byIdNewAccount).isEqualTo(newAccount);
    }

    @Test
    public void updateEvent_not_host() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);

        String name = "before";
        Event event = makeEvent(account, name, name + " dep", name + " dest");

        String newEmail = "newTest@test.com";
        Account newAccount = makeAccount(newEmail);
        String newName = "after";
        EventUpdateRequest eventUpdateRequest = makeEventDto(newAccount.getId(), newName, newName + " dep", newName + " dest");

        String token = jwtTokenProvider.createToken(newEmail);

        // Then
        mockMvc.perform(put("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("message", is("권한이 없습니다.")))
        ;

    }

    @Test
    public void updateEvent_wrong_account() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);

        String name = "before";
        Event event = makeEvent(account, name, name + " dep", name + " dest");

        String newEmail = "newTest@test.com";
        Account newAccount = makeAccount(newEmail);
        newAccount.setId(newAccount.getId() + 3);
        String newName = "after";
        EventUpdateRequest eventUpdateRequest = makeEventDto(newAccount.getId(), newName, newName + " dep", newName + " dest");

        String token = jwtTokenProvider.createToken(email);

        // Then
        mockMvc.perform(put("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("존재하지 않는 계정입니다.")))
        ;
    }

    @Test
    public void updateEvent_over_participants() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        String token = jwtTokenProvider.createToken(email);

        String name = "before";
        Event event = makeEvent(account, name, name + " dep", name + " dest");

        String newEmail = "newTest@test.com";
        Account newAccount = makeAccount(newEmail);
        Account newAccount1 = makeAccount(newEmail + "1");
        Account newAccount2 = makeAccount(newEmail + "2");
        Account newAccount3 = makeAccount(newEmail + "3");
        Account newAccount4 = makeAccount(newEmail + "4");

        EventUpdateRequest eventUpdateRequest = modelMapper.map(event, EventUpdateRequest.class);
        eventUpdateRequest.setHost_id(account.getId());
        eventUpdateRequest.addParticipants(account.getId());

        eventUpdateRequest.addParticipants(newAccount1.getId());
        eventUpdateRequest.addParticipants(newAccount2.getId());
        eventUpdateRequest.addParticipants(newAccount3.getId());
        eventUpdateRequest.addParticipants(newAccount4.getId());


        // Then
        mockMvc.perform(put("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("fail")))
        ;

        // Given
        Event byId = eventRepository.findById(event.getId()).get();

        // Then
        assertThat(byId.getHost().getEmail()).isEqualTo(account.getEmail());
        assertThat(byId.getHost().getId()).isEqualTo(account.getId());
        assertThat(byId.getId()).isEqualTo(event.getId());
        assertThat(byId.getName()).isEqualTo(eventUpdateRequest.getName());
        assertThat(byId.getDeparture()).isEqualTo(eventUpdateRequest.getDeparture());
        assertThat(byId.getDepartureTime()).isEqualTo(eventUpdateRequest.getDepartureTime());


        // Given
        Account byIdAccount = accountRepository.findById(account.getId()).get();
        Account byIdNewAccount = accountRepository.findById(newAccount.getId()).get();

        // Then
        assertThat(byIdAccount).isEqualTo(account);
        assertThat(byIdNewAccount).isEqualTo(newAccount);
    }

    @Test
    public void deleteEvent_no_authentication() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeEvent(account, "name", "depa", "dest");

        mockMvc.perform(delete("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;

        // Then
        mockMvc.perform(get("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
        ;

        Optional<Event> byId = eventRepository.findById(event.getId());
        assertThat(byId).isPresent();

        Optional<Account> byIdAccount = accountRepository.findById(account.getId());
        assertThat(byIdAccount).isNotEmpty();
    }

    @Test
    public void deleteEvent() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        Event event = makeEvent(account, "name", "depa", "dest");

        String token = jwtTokenProvider.createToken(email);

        mockMvc.perform(delete("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.event.links[0].rel", is("query-event-id")))
                .andExpect(jsonPath("$.event.links[1].rel", is("query-events-name")))
                .andExpect(jsonPath("$.event.links[2].rel", is("create-event")))
        ;

        // Then
        mockMvc.perform(get("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        Optional<Event> byId = eventRepository.findById(event.getId());
        assertThat(byId).isEmpty();

        Optional<Account> byIdAccount = accountRepository.findById(account.getId());
        assertThat(byIdAccount).isNotEmpty();
    }

    @Test
    public void deleteEvent_not_host() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        email += "ttt";
        makeAccount(email);
        Event event = makeEvent(account, "name", "depa", "dest");

        String token = jwtTokenProvider.createToken(email);

        mockMvc.perform(delete("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("message", is("권한이 없습니다.")))
        ;

        // Then
        mockMvc.perform(get("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<Event> byId = eventRepository.findById(event.getId());
        assertThat(byId).isPresent();
    }

    @Test
    public void createEvent_wrong_arrivalTime() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        EventUpdateRequest eventUpdateRequest = makeEventDto(account.getId(), "test", "testStart", "testEnd");
        eventUpdateRequest.setArrivalTime(eventUpdateRequest.getDepartureTime().minusHours(3));

        String token = jwtTokenProvider.createToken(email);

        // Then
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("fail")))
                .andExpect(jsonPath("$.errors[0].objectName", is("eventCreateRequest")))
                .andExpect(jsonPath("$.errors[0].code", is("wrongArrivalTime")))
                .andExpect(jsonPath("$.errors[0].defaultMessage", is("Arrival time is earlier than departure time")))
        ;
    }

    @Test
    public void createEvent_empty_input() throws Exception {
        // Given
        String email = "test@test.com";
        makeAccount(email);
        EventUpdateRequest eventUpdateRequest = new EventUpdateRequest();

        String token = jwtTokenProvider.createToken(email);

        // Then
        mockMvc.perform(post("/event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-AUTH-TOKEN", token)
                .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("message", is("fail")))
        ;
    }

    private Event makeEvent(Account account, String name, String departure, String destination) {
        Event event = Event.builder()
                .name(name)
                .departure(departure)
                .destination(destination)
                .departureTime(LocalDateTime.of(2021, 10, 12, 8, 0, 1))
                .arrivalTime(LocalDateTime.of(2021, 10, 12, 8, 45, 1))
                .host(account)
                .participants(new HashSet<>(Set.of(account)))
                .nowNum(1)
                .price(5000)
                .totalNum(4)
                .build();

        return eventRepository.save(event);
    }

    private EventUpdateRequest makeEventDto(Long id, String name, String departure, String destination) {
        EventUpdateRequest eventUpdateRequest = EventUpdateRequest.builder()
                .name(name)
                .departure(departure)
                .destination(destination)
                .departureTime(LocalDateTime.of(2021, 10, 12, 8, 0, 1))
                .arrivalTime(LocalDateTime.of(2021, 10, 12, 8, 45, 1))
                .host_id(id)
                .participants_id(new HashSet<>(Set.of(id)))
                .price(5000)
                .totalNum(4)
                .build();

        return eventUpdateRequest;
    }

    private EventCreateRequest makeEventCreatRequest(String name, String departure, String destination) {
        return EventCreateRequest.builder()
                .name(name)
                .departure(departure)
                .destination(destination)
                .departureTime(LocalDateTime.of(2021, 10, 12, 8, 0, 1))
                .arrivalTime(LocalDateTime.of(2021, 10, 12, 8, 45, 1))
                .price(5000)
                .totalNum(4)
                .build();

    }


    private Account makeAccount(String email) {
        Account account = Account.builder()
                .email(email)
                .password("pass")
                .hostEvents(new HashSet<>())
                .name("admin")
                .participantEvents(new HashSet<>())
                .roles(new HashSet<>(Set.of(AccountRole.ADMIN)))
                .build();

        account.encodePassword(passwordEncoder);
        Account save = accountRepository.save(account);
        save.setPassword("pass");
        return save;
    }
}