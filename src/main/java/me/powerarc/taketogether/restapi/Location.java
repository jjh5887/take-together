package me.powerarc.taketogether.restapi;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String name;
    private String telNo;
    private double noorLat;
    private double noorLon;
}
