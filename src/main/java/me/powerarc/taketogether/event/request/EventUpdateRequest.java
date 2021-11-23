package me.powerarc.taketogether.event.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EventUpdateRequest {
    @NotNull
    private String name;
    @NotNull
    private String departure;
    @NotNull
    private String destination;
    @NotNull
    private LocalDateTime departureTime;
    @NotNull
    private LocalDateTime arrivalTime;
    @Min(0)
    private int price;
    @Min(2)
    @Max(4)
    private int totalNum;

    @NotNull
    private Long host_id;
    @NotNull
    private Set<Long> participants_id = new HashSet<>();

    public void addParticipants(Long id) {
        participants_id.add(id);
    }
}
