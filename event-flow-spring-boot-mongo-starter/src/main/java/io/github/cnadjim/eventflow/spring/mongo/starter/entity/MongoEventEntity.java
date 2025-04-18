package io.github.cnadjim.eventflow.spring.mongo.starter.entity;


import io.github.cnadjim.eventflow.core.domain.message.Event;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "event-store")
@CompoundIndex(name = "aggregateId_timestamp_idx", def = "{'aggregateId': 1, 'timestamp': -1}")
public class MongoEventEntity {

    @Id
    private String id;
    private String topic;
    private Object payload;
    private Instant timestamp;
    private String aggregateId;

    public static MongoEventEntity fromEvent(Event event) {
        final MongoEventEntity eventEntity = new MongoEventEntity();
        eventEntity.setId(event.id());
        eventEntity.setTopic(event.topic().name());
        eventEntity.setPayload(event.payload());
        eventEntity.setTimestamp(event.timestamp());
        eventEntity.setAggregateId(event.aggregateId());
        return eventEntity;
    }

    public static Event toEvent(MongoEventEntity eventEntity) {
        return new Event(eventEntity.getId(), eventEntity.getPayload(), eventEntity.getTimestamp(), eventEntity.getAggregateId());
    }
}
