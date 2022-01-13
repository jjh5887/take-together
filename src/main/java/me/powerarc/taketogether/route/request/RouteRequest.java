package me.powerarc.taketogether.route.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.powerarc.taketogether.location.Location;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {
    Location start;
    Location end;
}
