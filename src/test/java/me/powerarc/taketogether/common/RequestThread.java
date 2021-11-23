package me.powerarc.taketogether.common;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.powerarc.taketogether.account.Account;
import me.powerarc.taketogether.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequestThread implements Runnable {

    MockMvc mockMvc;
    String url;
    String token;
    Event event;
    Account account;

    public RequestThread(String url, String token, Event event, Account account, MockMvc mockMvc) {
        this.url = url;
        this.token = token;
        this.event = event;
        this.account = account;
        this.mockMvc = mockMvc;
    }

    @SneakyThrows
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(url);
            mockMvc.perform(RestDocumentationRequestBuilders.get("/"  + url + "/{id}", event.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-AUTH-TOKEN", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("status", is(HttpStatus.OK.value())))
                    .andExpect(jsonPath("message", is("success")))
                    .andExpect(jsonPath("$.data.id").value(event.getId()))
                    .andExpect(jsonPath("$.data.departure").value(event.getDeparture()))
                    .andExpect(jsonPath("$.data.arrivalTime").value(event.getArrivalTime().toString()))
                    .andExpect(jsonPath("$.data.host.id").value(account.getId().toString()))
                    .andExpect(jsonPath("$.data.participants[0].id").value(account.getId().toString()))
                    .andExpect(jsonPath("$.data.links[1].href").value("http://localhost:8080/" + url + "/2"))
            ;
        }
    }
}
