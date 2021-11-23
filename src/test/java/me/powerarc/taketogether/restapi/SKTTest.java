package me.powerarc.taketogether.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.powerarc.taketogether.common.RestDocsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
class SKTTest {
    @Autowired
    SKT skt;

    @Test
    public void test() throws IOException {
        skt.getCoordinates("서울시청");
    }
}