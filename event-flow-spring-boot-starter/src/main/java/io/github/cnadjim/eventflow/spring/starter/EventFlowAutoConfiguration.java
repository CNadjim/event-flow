package io.github.cnadjim.eventflow.spring.starter;

import io.github.cnadjim.eventflow.spring.starter.config.EventFlowConfig;
import io.github.cnadjim.eventflow.spring.starter.config.JacksonConfig;
import io.github.cnadjim.eventflow.spring.starter.exception.GlobalExceptionHandler;
import io.github.cnadjim.eventflow.spring.starter.listener.ApplicationReadyListener;
import io.github.cnadjim.eventflow.spring.starter.property.EventFlowProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(value = {
        JacksonConfig.class,
        EventFlowConfig.class,
        GlobalExceptionHandler.class,
        ApplicationReadyListener.class
})
@EnableConfigurationProperties(EventFlowProperties.class)
public class EventFlowAutoConfiguration {
}
