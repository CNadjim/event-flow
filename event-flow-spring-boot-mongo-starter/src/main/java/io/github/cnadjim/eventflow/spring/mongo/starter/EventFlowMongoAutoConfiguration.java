package io.github.cnadjim.eventflow.spring.mongo.starter;

import io.github.cnadjim.eventflow.spring.mongo.starter.config.EventFlowConfig;
import io.github.cnadjim.eventflow.spring.mongo.starter.config.MongoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        MongoConfiguration.class,
        EventFlowConfig.class
})
@ConditionalOnProperty(prefix = "event-flow.mongo", name = "hostname")
public class EventFlowMongoAutoConfiguration {

}
