package io.github.cnadjim.eventflow.spring.starter.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.cnadjim.eventflow.core.domain.PayloadWrapper;

import java.io.IOException;

public class MessageSerializer extends StdSerializer<PayloadWrapper> {

    protected MessageSerializer(Class<PayloadWrapper> t) {
        super(t);
    }

    @Override
    public void serialize(PayloadWrapper message, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    }
}
