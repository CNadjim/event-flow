package io.github.cnadjim.eventflow.spring.mongo.starter.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cnadjim.eventflow.spring.starter.property.EventFlowProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

@EnableMongoRepositories(
        basePackages = "io.github.cnadjim.eventflow.spring.mongo.starter.repository",
        mongoTemplateRef = "eventFlowMongoTemplate"
)
public class MongoConfiguration {

    @Bean(name = "eventFlowMongoTemplateMongoClient")
    public MongoClient mongoClient(final EventFlowProperties eventFlowProperties) {

        final MongoCredential credential = MongoCredential
                .createCredential(
                        eventFlowProperties.getMongo().getUsername(),
                        eventFlowProperties.getMongo().getAuthenticationDatabase(),
                        eventFlowProperties.getMongo().getPassword()
                );

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .hosts(singletonList(new ServerAddress(eventFlowProperties.getMongo().getHostname(), eventFlowProperties.getMongo().getPort()))))
                .credential(credential)
                .build());
    }

    @Bean(name = "eventFlowMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier("eventFlowMongoTemplateMongoClient") final MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, "event-flow-database");
    }

    @Bean(name = "eventFlowMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("eventFlowMongoDBFactory") final MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
