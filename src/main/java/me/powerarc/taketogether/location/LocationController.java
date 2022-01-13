package me.powerarc.taketogether.location;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.restapi.Skt;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
public class LocationController {
    private final Skt skt;

    @GetMapping("/{location}")
    public ResponseEntity getLocations(@PathVariable String location) throws IOException {
        Location[] locations = skt.searchPOI(location);
        return ResponseEntity.ok(LocationResponse.builder()
                .status(HttpStatus.OK.value())
                .message("ok")
                .data(new LocationResource(Arrays.stream(locations).collect(Collectors.toList()), location))
                .build());
    }

}
