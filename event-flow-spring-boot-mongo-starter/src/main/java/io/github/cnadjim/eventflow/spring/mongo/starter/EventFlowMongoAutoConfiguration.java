package io.github.cnadjim.eventflow.spring.mongo.starter;

import io.github.cnadjim.eventflow.spring.mongo.starter.config.ApplicationMongoConfiguration;
import io.github.cnadjim.eventflow.spring.mongo.starter.config.EventFlowConfig;
import io.github.cnadjim.eventflow.spring.mongo.starter.config.EventFlowMongoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        ApplicationMongoConfiguration.class,
        EventFlowMongoConfiguration.class,
        EventFlowConfig.class
})
public class EventFlowMongoAutoConfiguration {

}
