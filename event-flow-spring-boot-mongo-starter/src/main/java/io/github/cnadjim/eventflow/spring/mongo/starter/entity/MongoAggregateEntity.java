package io.github.cnadjim.eventflow.spring.mongo.starter.entity;


import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "aggregate-store")
@CompoundIndexes(value = {
        @CompoundIndex(name = "aggregate_version_idx", def = "{'aggregateId': 1, 'version': -1}")
})
public class MongoAggregateEntity {
    private Long version;
    private Object payload;
    @Id
    private String aggregateId;

    public static MongoAggregateEntity fromAggregate(AggregateWrapper aggregate) {
        final MongoAggregateEntity aggregateEntity = new MongoAggregateEntity();
        aggregateEntity.setVersion(aggregate.version());
        aggregateEntity.setPayload(aggregate.payload());
        aggregateEntity.setAggregateId(aggregate.aggregateId());
        return aggregateEntity;
    }

    public static AggregateWrapper toAggregate(MongoAggregateEntity aggregateEntity) {
        return new AggregateWrapper(aggregateEntity.getVersion(), aggregateEntity.getPayload(), aggregateEntity.getAggregateId());
    }
}
