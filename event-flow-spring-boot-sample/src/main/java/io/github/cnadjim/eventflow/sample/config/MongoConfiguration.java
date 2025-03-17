package io.github.cnadjim.eventflow.sample.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

@Configuration
@EnableConfigurationProperties
@EnableMongoRepositories(
        basePackages = "io.github.cnadjim.eventflow.sample.repository",
        mongoTemplateRef = "applicationMongoTemplate"
)
public class MongoConfiguration {

    @Primary
    @Bean(name = "applicationMongoClient")
    public MongoClient mongoClient(MongoProperties mongoProperties) {

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

    @Primary
    @Bean(name = "applicationMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            final MongoProperties mongoProperties,
            @Qualifier("applicationMongoClient") MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Primary
    @Bean(name = "applicationMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("applicationMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
