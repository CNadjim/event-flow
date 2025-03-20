package io.github.cnadjim.eventflow.spring.mongo.starter.entity;


import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "event-store")
public class MongoEventEntity {

    @Id
    private String id;
    private String topic;
    private Object payload;
    private Instant timestamp;
    private String aggregateId;

    public static MongoEventEntity fromEvent(EventWrapper event) {
        final MongoEventEntity eventEntity = new MongoEventEntity();
        eventEntity.setId(event.id());
        eventEntity.setTopic(event.topic());
        eventEntity.setPayload(event.payload());
        eventEntity.setTimestamp(event.timestamp());
        eventEntity.setAggregateId(event.aggregateId());
        return eventEntity;
    }

    public static EventWrapper toEvent(MongoEventEntity eventEntity) {
        return new EventWrapper(eventEntity.getId(), eventEntity.getTopic(), eventEntity.getPayload(), eventEntity.getTimestamp(), eventEntity.getAggregateId());
    }
}
