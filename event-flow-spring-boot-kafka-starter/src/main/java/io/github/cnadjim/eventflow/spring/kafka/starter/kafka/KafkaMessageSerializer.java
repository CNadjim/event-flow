package io.github.cnadjim.eventflow.spring.kafka.starter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.PayloadWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@RequiredArgsConstructor
public class KafkaMessageSerializer<MESSAGE extends PayloadWrapper> implements Serializer<MESSAGE> {

    private final ObjectMapper objectMapper;

    @Override
    public byte[] serialize(String topic, MESSAGE message) {
        if (message == null) {
            return new byte[0];
        }

        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (Exception e) {
            throw new SerializationException("Failed to serialize message for topic: " + topic, ExceptionUtils.getRootCause(e));
        }
    }
}
