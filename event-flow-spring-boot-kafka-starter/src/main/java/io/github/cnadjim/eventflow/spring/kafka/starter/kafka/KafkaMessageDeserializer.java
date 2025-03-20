package io.github.cnadjim.eventflow.spring.kafka.starter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.PayloadWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@RequiredArgsConstructor
public class KafkaMessageDeserializer<MESSAGE extends PayloadWrapper> implements Deserializer<MESSAGE> {

    private final Class<MESSAGE> messageClass;
    private final ObjectMapper objectMapper;

    @Override
    public MESSAGE deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, messageClass);
        } catch (IOException exception) {
            throw new SerializationException("Error deserializing Message", exception);
        }
    }
}
