package me.powerarc.taketogether.event.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.powerarc.taketogether.event.Event;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.io.IOException;

public class EventPageResourceSerializer extends JsonSerializer<PagedModel<EntityModel<Event>>> {
    @Override
    public void serialize(PagedModel<EntityModel<Event>> entityModels, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    }
}
