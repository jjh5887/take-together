package me.powerarc.taketogether.taxi.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.powerarc.taketogether.event.request.EventCreateRequest;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaxiCreateRequest extends EventCreateRequest {

    @NotNull
    String kind;
}
