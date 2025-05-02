package io.github.cnadjim.eventflow.spring.mongo.starter.entity;


import io.github.cnadjim.eventflow.core.domain.message.Aggregate;
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

    public static MongoAggregateEntity fromAggregate(Aggregate aggregate) {
        final MongoAggregateEntity aggregateEntity = new MongoAggregateEntity();
        aggregateEntity.setVersion(aggregate.version());
        aggregateEntity.setPayload(aggregate.payload());
        aggregateEntity.setAggregateId(aggregate.aggregateId());
        return aggregateEntity;
    }

    public static Aggregate toAggregate(MongoAggregateEntity aggregateEntity) {
        return new Aggregate(aggregateEntity.getVersion(), aggregateEntity.getPayload(), aggregateEntity.getAggregateId());
    }
}
