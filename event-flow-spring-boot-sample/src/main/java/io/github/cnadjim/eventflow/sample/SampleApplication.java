package io.github.cnadjim.eventflow.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@EnableMongoRepositories(basePackages = "io.github.cnadjim.eventflow.sample.repository", mongoTemplateRef = "mongoTemplate")
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
