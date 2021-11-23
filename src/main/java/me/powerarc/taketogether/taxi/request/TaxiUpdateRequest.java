package me.powerarc.taketogether.taxi.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.powerarc.taketogether.event.request.EventUpdateRequest;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaxiUpdateRequest extends EventUpdateRequest {

    @NotNull
    String kind;
}
