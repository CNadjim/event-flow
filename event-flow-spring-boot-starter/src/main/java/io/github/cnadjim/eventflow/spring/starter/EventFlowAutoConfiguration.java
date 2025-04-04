package io.github.cnadjim.eventflow.spring.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = EventFlowAutoConfiguration.class)
public class EventFlowAutoConfiguration {
}
