package io.github.cnadjim.eventflow.spring.starter.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.cnadjim.eventflow.core.domain.Message;

import java.io.IOException;

public class MessageSerializer extends StdSerializer<Message> {

    protected MessageSerializer(Class<Message> t) {
        super(t);
    }

    @Override
    public void serialize(Message message, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    }
}
