package io.github.cnadjim.eventflow.spring.kafka.starter.spi;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import io.github.cnadjim.eventflow.spring.kafka.starter.domain.EventFlowKafkaConsumer;
import io.github.cnadjim.eventflow.spring.kafka.starter.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
public class KafkaMessageBus implements MessageBus {

    private final KafkaService kafkaService;
    private final KafkaProducer<String, Message> kafkaProducer;

    public KafkaMessageBus(final KafkaService kafkaService,
                           final KafkaProducer<String, Message> kafkaProducer) {
        this.kafkaService = kafkaService;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publish(Message message) {
        final ProducerRecord<String, Message> record = new ProducerRecord<>(message.topic().name(), message.id(), message);
        kafkaProducer.send(record);
    }

    @Override
    public <MESSAGE extends Message> void subscribe(MessageSubscriber<MESSAGE> messageSubscriber) {
        final Topic topic = messageSubscriber.topic();
        final EventFlowKafkaConsumer eventFlowKafkaConsumer = kafkaService.getConsumerByTopic(topic);
        eventFlowKafkaConsumer.addSubscriber(messageSubscriber);
    }
}
