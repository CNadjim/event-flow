package io.github.cnadjim.eventflow.spring.mongo.starter.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "aggregate-store")
@CompoundIndex(name = "aggregate_version_idx", def = "{'aggregateId': 1, 'version': -1}")
public class MongoAggregateEntity {

    @Id
    private String aggregateId;
    private Long version;
    private Object payload;
}
