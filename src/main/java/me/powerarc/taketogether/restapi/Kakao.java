package me.powerarc.taketogether.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.exception.WebException;
import me.powerarc.taketogether.location.Location;
import me.powerarc.taketogether.route.Route;
import me.powerarc.taketogether.route.RouteKind;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class Kakao {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String appKey = "KakaoAK c0bb6385f4c939f16ca92491ea62689d";
    private final String APP_KEY = "Authorization";

    private final String ROUTE_URL = "https://apis-navi.kakaomobility.com/v1/directions";

    private final String ORIGIN = "origin";
    private final String DESTINATION = "destination";
    private final String SUMMARY = "summary";

    public Route searchRoute(Location departure, Location destination) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(APP_KEY, appKey);
        HttpEntity entity = new HttpEntity(httpHeaders);
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(ROUTE_URL)
                .queryParam(ORIGIN, departure.getNoorLon() + "," + departure.getNoorLat())
                .queryParam(DESTINATION, destination.getNoorLon() + "," + destination.getNoorLat())
                .queryParam(SUMMARY, true)
                .build();
        ResponseEntity<Map> exchange = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.");
        }
        JSONObject summary = new JSONObject(exchange.getBody()).getJSONArray("routes").getJSONObject(0).getJSONObject("summary");
        Route route = Route.builder()
                .departure(departure)
                .destination(destination)
                .taxiFare(summary.getJSONObject("fare").getInt("taxi"))
                .totalFare(summary.getJSONObject("fare").getInt("toll"))
                .totalDistance(summary.getInt("distance"))
                .totalTime(summary.getInt("duration"))
                .kind(RouteKind.KAKAO)
                .build();

        return route;
    }

}
