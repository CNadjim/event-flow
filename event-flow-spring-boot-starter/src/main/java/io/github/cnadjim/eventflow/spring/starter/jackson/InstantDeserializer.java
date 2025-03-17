package io.github.cnadjim.eventflow.spring.starter.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

import static io.github.cnadjim.eventflow.spring.starter.config.JacksonConfig.DATE_TIME_FORMATTER;
import static io.github.cnadjim.eventflow.spring.starter.config.JacksonConfig.ZONE_OFFSET;


public class InstantDeserializer extends StdDeserializer<Instant> {

    public InstantDeserializer() {
        this(null);
    }

    public InstantDeserializer(Class<Instant> t) {
        super(t);
    }

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        final String dateAsString = jsonParser.readValueAs(String.class);
        final LocalDateTime localDateTime = LocalDateTime.parse(dateAsString, DATE_TIME_FORMATTER);
        return localDateTime.toInstant(ZONE_OFFSET);
    }
}
