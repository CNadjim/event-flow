package io.github.cnadjim.eventflow.spring.rabbitmq.starter.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

@Slf4j
public class RabbitMqMessageConverter extends Jackson2JsonMessageConverter {

    private final ObjectMapper objectMapper;

    public RabbitMqMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public Object fromMessage(org.springframework.amqp.core.Message message, Object conversionHint) {
        try {
            byte[] body = message.getBody();
            return objectMapper.readValue(body, Message.class);
        } catch (Exception e) {
            log.error("Failed to convert message: {}", ExceptionUtils.getRootCauseMessage(e));
            throw new MessageConversionException("Failed to convert message", ExceptionUtils.getRootCause(e));
        }
    }

    @Override
    protected org.springframework.amqp.core.Message createMessage(Object object, MessageProperties messageProperties) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(object);
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            messageProperties.setContentEncoding("UTF-8");
            return new org.springframework.amqp.core.Message(bytes, messageProperties);
        } catch (Exception e) {
            log.error("Failed to create message: {}", ExceptionUtils.getRootCauseMessage(e));
            throw new MessageConversionException("Failed to create message", ExceptionUtils.getRootCause(e));
        }
    }
}
