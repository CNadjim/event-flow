package io.github.cnadjim.eventflow.spring.rabbitmq.starter.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.lang.NonNull;

@Slf4j
@RequiredArgsConstructor
public class RabbitMqMessageConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    @NonNull
    @Override
    public Message toMessage(@NonNull Object object,@NonNull MessageProperties messageProperties) throws MessageConversionException {
        try {
            byte[] bytes = objectMapper.writerFor(Object.class).writeValueAsBytes(object);
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            messageProperties.setContentEncoding("UTF-8");
            return new org.springframework.amqp.core.Message(bytes, messageProperties);
        } catch (Exception e) {
            throw new MessageConversionException("Failed to create message", ExceptionUtils.getRootCause(e));
        }
    }
    @NonNull
    @Override
    public Object fromMessage(@NonNull Message message) throws MessageConversionException {
        try {
            return objectMapper.readValue(message.getBody(), io.github.cnadjim.eventflow.core.domain.message.Message.class);
        }  catch (Exception e) {
            throw new MessageConversionException("Failed to create message", ExceptionUtils.getRootCause(e));
        }
    }
}
