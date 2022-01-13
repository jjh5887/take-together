package me.powerarc.taketogether.restapi;

import me.powerarc.taketogether.common.RestDocsConfiguration;
import me.powerarc.taketogether.location.Location;
import me.powerarc.taketogether.route.Route;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
class SktTest {
    @Autowired
    Skt skt;

    @Test
    public void test() throws IOException {
        Location[] locations = skt.searchPOI("서울시청");
        Location[] locations2 = skt.searchPOI("계양구청");
        Route route = skt.searchRoute(locations[0], locations2[0]);
        System.out.println(route.getDeparture().getName() + ", " + route.getDestination().getName() + " " + route.getTotalDistance() + " " + route.getTotalFare() + " " + route.getTaxiFare());
    }
}