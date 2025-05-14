package io.github.cnadjim.eventflow.spring.mongo.starter.entity;


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
}
