package me.powerarc.taketogether.account.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.powerarc.taketogether.account.Account;

import java.io.IOException;
import java.util.Set;

public class AccountSetSerializer extends JsonSerializer<Set<Account>> {
    @Override
    public void serialize(Set<Account> accounts, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (Account account : accounts) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", account.getId());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}
