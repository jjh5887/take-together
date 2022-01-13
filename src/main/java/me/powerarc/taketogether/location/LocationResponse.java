package me.powerarc.taketogether.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationResponse {
    private int status;
    private String message;
    private LocationResource data;
}
