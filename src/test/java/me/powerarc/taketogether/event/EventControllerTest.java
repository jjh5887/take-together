package me.powerarc.taketogether.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.account.AccountRepository;
import me.powerarc.taketogether.account.AccountRole;
import me.powerarc.taketogether.common.RequestThread;
import me.powerarc.taketogether.common.RestDocsConfiguration;
import me.powerarc.taketogether.event.request.EventCreateRequest;
import me.powerarc.taketogether.event.request.EventUpdateRequest;
import me.powerarc.taketogether.jwt.JwtTokenProvider;
import me.powerarc.taketogether.taxi.Taxi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
@Import(RestDocsConfiguration.class)
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
    public void getEventAndTaxiById() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeTaxi(account, "test", "Incheon", "Seoul");
        account.addEvent(event);
        accountRepository.save(account);

        String token = jwtTokenProvider.createToken(account.getEmail());
        // Then
        Thread threadEvent = new Thread(new RequestThread("event", token, event, account, mockMvc));
        Thread threadTaxi = new Thread(new RequestThread("taxi", token, event, account, mockMvc));
        threadEvent.start();
        threadTaxi.start();
    }

    @Test
    public void getEventById() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeEvent(account, "test", "Incheon", "Seoul");
        account.addEvent(event);
        accountRepository.save(account);

        String token = jwtTokenProvider.createToken(account.getEmail());

        // Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/event/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.data.id").value(event.getId()))
                .andExpect(jsonPath("$.data.departure").value(event.getDeparture()))
                .andExpect(jsonPath("$.data.arrivalTime").value(event.getArrivalTime().toString()))
                .andExpect(jsonPath("$.data.host.id").value(account.getId().toString()))
                .andExpect(jsonPath("$.data.participants[0].id").value(account.getId().toString()))
                .andDo(document("get-event",
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("사용자 인증용 토큰\n\n" +
                                        "인증된 토큰이라면 인증시에 요청 가능한 링크 추가 제공")
                        ),
                        pathParameters(
                                parameterWithName("id").description("이벤트 id")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        ).andWithPrefix("data.",
                                fieldWithPath("id").description("이벤트 id"),
                                fieldWithPath("name").description("이벤트 이름"),
                                fieldWithPath("departure").description("이벤트 출발지"),
                                fieldWithPath("destination").description("이벤트 도착지"),
                                fieldWithPath("departureTime").description("이벤트 출발시간"),
                                fieldWithPath("arrivalTime").description("이벤트 도착시간"),
                                fieldWithPath("price").description("이벤트 가격"),
                                fieldWithPath("totalNum").description("이벤트 참여 가능한 인원"),
                                fieldWithPath("nowNum").description("이벤트 현재 참여 인원"),
                                fieldWithPath("host.id").description("이벤트 주인 id"),
                                fieldWithPath("participants[].id").description("이벤트 참여자 id"),
                                fieldWithPath("links[].rel").description("링크 이름\n\n" +
                                        "self: 자기 자신 +\n" +
                                        "get-event: 이벤트 조회 +\n" +
                                        "get-events-name: 이름으로 이벤트 조회 +\n" +
                                        "create-event: 이벤트 생성 +\n" +
                                        "update-event: 이벤트 수정 +\n" +
                                        "delete-event: 이벤트 삭제 +\n" +
                                        "profile: REST-API-Guide"),
                                fieldWithPath("links[].href").description("링크")
                        )
                ))
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
                .andDo(document("error",
                        responseFields(
                                fieldWithPath("status").description("에러 코드"),
                                fieldWithPath("message").description("에러 원인")
                        )))
        ;
    }

    @Test
    public void getEventByName() throws Exception {
        // Given
        Account account = makeAccount("test@test.com");
        Event event = makeEvent(account, "test", "Incheon", "Seoul");
        account.addEvent(event);
        for (int i = 0; i < 100; i++) {
            account.addEvent(makeEvent(account, "test" + i, "Incheon" + i, "Seoul" + i));
        }
        accountRepository.save(account);

        String token = jwtTokenProvider.createToken(account.getEmail());

        // Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/event/name/{name}", event.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", token)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.data.content[0].id").value(event.getId()))
                .andExpect(jsonPath("$.data.content[0].name").value(event.getName()))
                .andExpect(jsonPath("$.data.content[0].departure").value(event.getDeparture()))
                .andExpect(jsonPath("$.data.content[0].arrivalTime").value(event.getArrivalTime().toString()))
                .andExpect(jsonPath("$.data.content[0].host.id").value(account.getId().toString()))
                .andExpect(jsonPath("$.data.content[0].participants[0].id").value(account.getId().toString()))
                .andDo(document("get-events-name",
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("사용자 인증용 토큰\n\n" +
                                        "인증된 토큰이라면 인증시에 요청 가능한 링크 추가 제공")
                        ),
                        pathParameters(
                                parameterWithName("name").description("이벤트 이름")
                        )
                        ,
                        requestParameters(
                                parameterWithName("page").description("요청 페이지 \n\n페이지는 0부터 시작"),
                                parameterWithName("size").description("페이지 당 이벤트 개수")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        ).andWithPrefix("data.",
                                fieldWithPath("content[]").description("이벤트 목록\n\n link:#resources-event_response_fields[이벤트 조회 참조]"),
                                fieldWithPath("links[].rel").description("링크 이름\n\n" +
                                        "first: 첫 페이지 +\n" +
                                        "self: 현재 페이지 +\n" +
                                        "next: 다음 페이지 +\n" +
                                        "last: 마지막 페이지 + \n" +
                                        "profile: REST-API-Guide"),
                                fieldWithPath("links[].href").description("링크")
                        ).andWithPrefix("data.page.",
                                fieldWithPath("size").description("페이지 당 이벤트 수"),
                                fieldWithPath("totalElements").description("전체 이벤트 수"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("number").description("전체 페이지 번호")
                        )
                ))
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
                .andExpect(jsonPath("$.data.content").isEmpty())
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
                .andExpect(jsonPath("$.data.content[*].departure", hasItem(departure)))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
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
                .andExpect(jsonPath("$.data.content[*].destination", hasItem(destination)))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
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
                .andExpect(jsonPath("$.data.links[0].rel", is("profile")))
                .andDo(document("create-event",
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("사용자 인증용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이벤트 이름"),
                                fieldWithPath("departure").description("이벤트 출발지"),
                                fieldWithPath("destination").description("이벤트 도착지"),
                                fieldWithPath("departureTime").description("이벤트 출발 시간").type(LocalDateTime.class),
                                fieldWithPath("arrivalTime").description("이벤트 도착 시간").type(LocalDateTime.class),
                                fieldWithPath("price").description("이벤트 가격"),
                                fieldWithPath("totalNum").description("이벤트 인원")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        ).andWithPrefix("data.",
                                fieldWithPath("id").description("이벤트 id"),
                                fieldWithPath("name").description("이벤트 이름"),
                                fieldWithPath("departure").description("이벤트 출발지"),
                                fieldWithPath("destination").description("이벤트 도착지"),
                                fieldWithPath("departureTime").description("이벤트 출발시간"),
                                fieldWithPath("arrivalTime").description("이벤트 도착시간"),
                                fieldWithPath("price").description("이벤트 가격"),
                                fieldWithPath("totalNum").description("이벤트 참여 가능한 인원"),
                                fieldWithPath("nowNum").description("이벤트 현재 참여 인원"),
                                fieldWithPath("host.id").description("이벤트 주인 id"),
                                fieldWithPath("participants[].id").description("이벤트 참여자 id"),
                                fieldWithPath("links[].rel").description("링크 이름\n\n" +
                                        "self: 자기 자신 +\n" +
                                        "get-event: 이벤트 조회 +\n" +
                                        "get-events-name: 이름으로 이벤트 조회 +\n" +
                                        "create-event: 이벤트 생성 +\n" +
                                        "update-event: 이벤트 수정 +\n" +
                                        "delete-event: 이벤트 삭제 + \n" +
                                        "profile: REST-API-Guide"),
                                fieldWithPath("links[].href").description("링크")
                        )
                ))

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
        mockMvc.perform(RestDocumentationRequestBuilders.put("/event/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", token)
                        .content(objectMapper.writeValueAsString(eventUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andDo(document("update-event",
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("사용자 인증용 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("이벤트 id")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이벤트 이름"),
                                fieldWithPath("departure").description("이벤트 출발지"),
                                fieldWithPath("destination").description("이벤트 도착지"),
                                fieldWithPath("departureTime").description("이벤트 출발 시간").type(LocalDateTime.class),
                                fieldWithPath("arrivalTime").description("이벤트 도착 시간").type(LocalDateTime.class),
                                fieldWithPath("price").description("이벤트 가격"),
                                fieldWithPath("totalNum").description("이벤트 인원"),
                                fieldWithPath("host_id").description("이벤트 주인 id"),
                                fieldWithPath("participants_id[]").description("이벤트 참여자 id")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        ).andWithPrefix("data.",
                                fieldWithPath("id").description("이벤트 id"),
                                fieldWithPath("name").description("이벤트 이름"),
                                fieldWithPath("departure").description("이벤트 출발지"),
                                fieldWithPath("destination").description("이벤트 도착지"),
                                fieldWithPath("departureTime").description("이벤트 출발시간"),
                                fieldWithPath("arrivalTime").description("이벤트 도착시간"),
                                fieldWithPath("price").description("이벤트 가격"),
                                fieldWithPath("totalNum").description("이벤트 참여 가능한 인원"),
                                fieldWithPath("nowNum").description("이벤트 현재 참여 인원"),
                                fieldWithPath("host.id").description("이벤트 주인 id"),
                                fieldWithPath("participants[].id").description("이벤트 참여자 id"),
                                fieldWithPath("links[].rel").description("링크 이름\n\n" +
                                        "self: 자기 자신 +\n" +
                                        "get-event: 이벤트 조회 +\n" +
                                        "get-events-name: 이름으로 이벤트 조회 +\n" +
                                        "create-event: 이벤트 생성 +\n" +
                                        "update-event: 이벤트 수정 +\n" +
                                        "delete-event: 이벤트 삭제 +\n" +
                                        "profile: REST-API-Guide"),
                                fieldWithPath("links[].href").description("링크")
                        )
                ))

        ;

        // Given
        Event byId = (Event) eventRepository.findById(event.getId()).get();

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
                .andExpect(jsonPath("message").exists())
        ;

        // Given
        Event byId = (Event) eventRepository.findById(event.getId()).get();

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

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/event/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("message", is("success")))
                .andExpect(jsonPath("$.data.links[0].rel", is("profile")))
                .andExpect(jsonPath("$.data.links[1].rel", is("get-event")))
                .andExpect(jsonPath("$.data.links[2].rel", is("get-events-name")))
                .andExpect(jsonPath("$.data.links[3].rel", is("create-event")))
                .andDo(document("delete-event",
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("사용자 인증용 토큰\n\n" +
                                        "인증된 토큰이라면 인증시에 요청 가능한 링크 추가 제공")
                        ),
                        pathParameters(
                                parameterWithName("id").description("이벤트 id")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        ).andWithPrefix("data.links[]",
                                fieldWithPath("rel").description("링크 이름\n\n" +
                                        "self: 자기 자신 +\n" +
                                        "get-event: 이벤트 조회 +\n" +
                                        "get-events-name: 이름으로 이벤트 조회 +\n" +
                                        "create-event: 이벤트 생성 +\n" +
                                        "update-event: 이벤트 수정 +\n" +
                                        "delete-event: 이벤트 삭제 +\n" +
                                        "profile: REST-API-Guide"),
                                fieldWithPath("href").description("링크")
                        )
                ))
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
                .andExpect(jsonPath("message").exists())
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
                .andExpect(jsonPath("message").exists())
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

        return (Event) eventRepository.save(event);
    }

    private Event makeTaxi(Account account, String name, String departure, String destination) {
        Taxi taxi = Taxi.builder()
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
                .kind("test")
                .build();

        return (Event) eventRepository.save(taxi);
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