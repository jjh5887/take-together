package me.powerarc.taketogether.route;

import lombok.*;
import me.powerarc.taketogether.location.Location;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route implements Serializable {
    private Location departure;
    private Location destination;
    private int totalTime;
    private int totalDistance;
    private int totalFare;
    private int taxiFare;
    private RouteKind kind;
}
