package io.github.cnadjim.eventflow.spring.mongo.starter.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
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
public class EventFlowMongoConfiguration {

    @Bean(name = "eventFlowMongoTemplateMongoClient")
    public MongoClient mongoClient(final MongoProperties mongoProperties) {

        final MongoCredential credential = MongoCredential
                .createCredential(
                        mongoProperties.getUsername(),
                        mongoProperties.getAuthenticationDatabase(),
                        mongoProperties.getPassword()
                );

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
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
