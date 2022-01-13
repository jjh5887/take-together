package me.powerarc.taketogether.route.response;

import lombok.Builder;
import lombok.Data;
import me.powerarc.taketogether.route.RouteResource;

@Data
@Builder
public class RouteResponse {
    private int status;
    private String message;
    private RouteResource data;
}
