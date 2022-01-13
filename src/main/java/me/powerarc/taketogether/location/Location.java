package me.powerarc.taketogether.location;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location implements Serializable {
    private String name;
    private String telNo;
    private double noorLat;
    private double noorLon;
}
