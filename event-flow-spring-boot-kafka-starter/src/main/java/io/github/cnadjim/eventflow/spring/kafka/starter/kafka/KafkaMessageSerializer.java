package io.github.cnadjim.eventflow.spring.kafka.starter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class KafkaMessageSerializer implements Serializer<Message> {

    private final ObjectMapper objectMapper;

    @Override
    public byte[] serialize(String topic, Message message) {
        if (isNull(message)) {
            return new byte[0];
        }
        try {
            return objectMapper.writerFor(Object.class).writeValueAsBytes(message);
        } catch (Exception e) {
            throw new SerializationException("Failed to serialize message for topic: " + topic, ExceptionUtils.getRootCause(e));
        }
    }
}
