package me.powerarc.taketogether.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
        mockMvc.perform(get("/event/id/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event.getId()))
                .andExpect(jsonPath("departure").value(event.getDeparture()))
                .andExpect(jsonPath("arrivalTime").value(event.getArrivalTime().toString()))
                .andExpect(jsonPath("$.host.id").value(account.getId().toString()))
                .andExpect(jsonPath("$.participants[0].id").value(account.getId().toString()))
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
                .andExpect(jsonPath("$[0].id").value(event.getId()))
                .andExpect(jsonPath("$[0].name").value(event.getName()))
                .andExpect(jsonPath("$[0].departure").value(event.getDeparture()))
                .andExpect(jsonPath("$[0].arrivalTime").value(event.getArrivalTime().toString()))
                .andExpect(jsonPath("$[0].host.id").value(account.getId().toString()))
                .andExpect(jsonPath("$[0].participants[0].id").value(account.getId().toString()))
                .andExpect(jsonPath("$[1]").doesNotExist())
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
                .andExpect(jsonPath("$[*].departure", hasItem(departure)))
                .andExpect(jsonPath("$[*]", hasSize(2)))
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
                .andExpect(jsonPath("$[*].destination", hasItem(destination)))
                .andExpect(jsonPath("$[*]", hasSize(2)))
        ;
    }

    @Test
    public void createEvent() throws Exception {
        // Given
        String email = "test@test.com";
        Account account = makeAccount(email);
        int id = Math.toIntExact(account.getId());
        String name = "test";
        String departure = "Incheon";
        String destination = "Seoul";
        EventDto eventDto = makeEventDto(account, name, departure, destination);

        // Then
        mockMvc.perform(post("/event/" + email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("departure", is(departure)))
                .andExpect(jsonPath("destination", is(destination)))
                .andExpect(jsonPath("$.host.id", is(id)))
                .andExpect(jsonPath("$.participants[0].id", is(id)))
        ;

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
        EventDto eventDto = makeEventDto(newAccount, newName, newName + " dep", newName + " dest");
        int id = Math.toIntExact(newAccount.getId());

        // Then
        mockMvc.perform(put("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(eventDto.getName())))
                .andExpect(jsonPath("departure", is(eventDto.getDeparture())))
                .andExpect(jsonPath("destination", is(eventDto.getDestination())))
                .andExpect(jsonPath("$.host.id", is(id)))
                .andExpect(jsonPath("$.participants[0].id", is(id)))
        ;

        // Given
        Account byIdAccount = accountRepository.findById(account.getId()).get();
        Account byIdNewAccount = accountRepository.findById(newAccount.getId()).get();

        // Then
        assertThat(byIdAccount).isEqualTo(account);
        assertThat(byIdNewAccount).isEqualTo(newAccount);

    }

    @Test
    public void deleteEvent() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeEvent(account, "name", "depa", "dest");

        mockMvc.perform(delete("/event/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        // Then
        mockMvc.perform(get("/event/id/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Optional<Event> byId = eventRepository.findById(event.getId());
        assertThat(byId).isEmpty();

        Optional<Account> byIdAccount = accountRepository.findById(account.getId());
        assertThat(byIdAccount).isNotEmpty();
    }


    private Event makeEvent(Account account, String name, String departure, String destination) {
        Event event = Event.builder()
                .name(name)
                .departure(departure)
                .destination(destination)
                .arrivalTime(LocalDateTime.of(2021, 10, 12, 8, 0, 1))
                .departureTime(LocalDateTime.of(2021, 10, 12, 8, 45, 1))
                .host(account)
                .participants(new HashSet<>(Set.of(account)))
                .nowNum(1)
                .price(5000)
                .totalNum(4)
                .build();

        return eventRepository.save(event);
    }

    private EventDto makeEventDto(Account account, String name, String departure, String destination) {
        EventDto eventDto = EventDto.builder()
                .name(name)
                .departure(departure)
                .destination(destination)
                .arrivalTime(LocalDateTime.of(2021, 10, 12, 8, 0, 1))
                .departureTime(LocalDateTime.of(2021, 10, 12, 8, 45, 1))
                .host(account)
                .participants(new HashSet<>(Set.of(account)))
                .nowNum(1)
                .price(5000)
                .totalNum(4)
                .build();

        return eventDto;
    }


    private Account makeAccount(String email) {
        Account account = Account.builder()
                .email(email)
                .password("pass")
                .hostEvents(new HashSet<>())
                .name("admin")
                .participantEvents(new HashSet<>())
                .build();
        return accountRepository.save(account);
    }
}