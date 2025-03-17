package io.github.cnadjim.eventflow.spring.starter.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "event-flow")
public class EventFlowProperties {
    private EventFlowKafkaProperties kafka;
    private EventFlowMongoProperties mongo;
    private EventFlowRabbitMqProperties rabbitmq;

    @Data
    public static class EventFlowMongoProperties {
        private String hostname;
        private int port;
        private String username;
        private char[] password;
        private String authenticationDatabase;
    }

    @Data
    public static class EventFlowKafkaProperties {
        private String hostname;
        private int port;
    }


    @Data
    public static class EventFlowRabbitMqProperties {
        private String host;
        private int port;
        private String username;
        private char[] password;
        private String virtualHost;
    }
}
