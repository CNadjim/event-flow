package io.github.cnadjim.eventflow.spring.mongo.starter.mapper;

import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.topic.MessageTopic;
import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;

public class EventMapper {

    public static MongoEventEntity toEntity(Event event) {
        final MongoEventEntity eventEntity = new MongoEventEntity();
        eventEntity.setId(event.id());
        eventEntity.setTopic(event.topic().name());
        eventEntity.setPayload(event.payload());
        eventEntity.setTimestamp(event.timestamp());
        eventEntity.setAggregateId(event.aggregateId());
        return eventEntity;
    }

    public static Event toDomain(MongoEventEntity eventEntity) {
        return new Event(eventEntity.getId(), eventEntity.getPayload(), eventEntity.getTimestamp(), eventEntity.getAggregateId(), new MessageTopic(eventEntity.getTopic()));
    }
}
