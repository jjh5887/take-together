package me.powerarc.taketogether.route;

import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.route.request.RouteRequest;
import me.powerarc.taketogether.route.response.RouteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/route", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {
    private final RouteService routeService;

    @GetMapping
    public ResponseEntity getRoutes(@RequestBody RouteRequest request) throws InterruptedException {
        List<Route> routes = routeService.getRoutes(request.getStart(), request.getEnd());
        return ResponseEntity.ok(RouteResponse.builder()
                .status(HttpStatus.OK.value())
                .message("ok")
                .data(new RouteResource(routes)).build());
    }

    @GetMapping("/test")
    public ResponseEntity getRoutesWithoutThread(@RequestBody RouteRequest request) throws IOException{
        List<Route> routes = routeService.getRoutesWithoutThread(request.getStart(), request.getEnd());
        return ResponseEntity.ok(RouteResponse.builder()
                .status(HttpStatus.OK.value())
                .message("ok")
                .data(new RouteResource(routes)).build());
    }
}
