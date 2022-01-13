package me.powerarc.taketogether.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.powerarc.taketogether.exception.WebException;
import me.powerarc.taketogether.location.Location;
import me.powerarc.taketogether.route.Route;
import me.powerarc.taketogether.route.RouteKind;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class Skt {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String appKey = "l7xx9c2b3b69d1714f7db6f27ff6d6e5004a";
    private final String APP_KEY = "appKey";
    private final String VERSION = "version";

    private final String POI_URL = "https://apis.openapi.sk.com/tmap/pois";
    private final String ROUTE_URL = "https://apis.openapi.sk.com/tmap/routes";

    private final String SEARCH_KEYWORD = "searchKeyword";
    private final String START_X = "startX";
    private final String START_Y = "startY";
    private final String END_X = "endX";
    private final String END_Y = "endY";
    private final String TOTAL_VALUE = "totalValue";

    public Location[] searchPOI(String keyword) throws JsonProcessingException, WebException {
        HttpEntity entity = new HttpEntity(new HttpHeaders());
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(POI_URL)
                .queryParam(VERSION, 1)
                .queryParam(SEARCH_KEYWORD, keyword)
                .queryParam(APP_KEY, appKey).build();
        ResponseEntity<Map> exchange = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.");
        }
        JSONArray pois = new JSONObject(exchange.getBody()).getJSONObject("searchPoiInfo").getJSONObject("pois").getJSONArray("poi");
        Location[] locations = objectMapper.readValue(pois.toString(), Location[].class);
        return locations;
    }

    public Route searchRoute(Location departure, Location destination) throws JsonProcessingException {
        HttpEntity entity = new HttpEntity(new HttpHeaders());
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(ROUTE_URL)
                .queryParam(VERSION, 1)
                .queryParam(TOTAL_VALUE, 2)
                .queryParam(START_X, departure.getNoorLon())
                .queryParam(START_Y, departure.getNoorLat())
                .queryParam(END_X, destination.getNoorLon())
                .queryParam(END_Y, destination.getNoorLat())
                .queryParam(APP_KEY, appKey).build();
        ResponseEntity<Map> exchange = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Map.class);
        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new WebException(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.");
        }
        JSONObject jsonRoute = new JSONObject(exchange.getBody()).getJSONArray("features").getJSONObject(0).getJSONObject("properties");
        Route route = objectMapper.readValue(jsonRoute.toString(), Route.class);
        route.setDeparture(departure);
        route.setDestination(destination);
        route.setKind(RouteKind.SKT);
        return route;
    }

}
