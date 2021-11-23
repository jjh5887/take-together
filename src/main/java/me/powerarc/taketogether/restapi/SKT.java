package me.powerarc.taketogether.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class SKT {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    private final String appKey = "l7xx9c2b3b69d1714f7db6f27ff6d6e5004a";

    public double getCoordinates(String keyword) throws IOException {
        String url = "https://apis.openapi.sk.com/tmap/pois";
        HttpEntity entity = new HttpEntity(new HttpHeaders());

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("version", 1)
                .queryParam("searchKeyword", keyword)
                .queryParam("appKey", appKey).build();
        ResponseEntity<Map> exchange = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
        JSONObject jsonObject = new JSONObject(exchange.getBody());
        JSONArray pois = jsonObject.getJSONObject("searchPoiInfo").getJSONObject("pois").getJSONArray("poi");
        Location[] locations = objectMapper.readValue(pois.toString(), Location[].class);
        for (Location location : locations) {
            System.out.println(location.getName() + " " + location.getTelNo() + " " + location.getNoorLat());
        }

        return 0.0;
    }

}
