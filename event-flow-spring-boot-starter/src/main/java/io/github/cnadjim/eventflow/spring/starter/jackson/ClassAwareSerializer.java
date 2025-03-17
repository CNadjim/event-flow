package io.github.cnadjim.eventflow.spring.starter.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ClassAwareSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("_class", value.getClass().getName());
        JsonSerializer<Object> defaultSerializer = provider.findValueSerializer(value.getClass());
        defaultSerializer.serialize(value, gen, provider);
        gen.writeEndObject();
    }
}
