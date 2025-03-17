package io.github.cnadjim.eventflow.spring.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cnadjim.eventflow.spring.starter.jackson.ClassAwareSerializer;
import io.github.cnadjim.eventflow.spring.starter.jackson.InstantDeserializer;
import io.github.cnadjim.eventflow.spring.starter.jackson.InstantSerializer;
import io.github.cnadjim.eventflow.spring.starter.jackson.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class JacksonConfig {

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static ZoneId ZONE_ID = ZoneId.systemDefault();
    public final static ZoneOffset ZONE_OFFSET = ZONE_ID.getRules().getOffset(LocalDateTime.now());
    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern(DATE_FORMAT)
            .withLocale(Locale.FRANCE)
            .withZone(ZONE_ID);


    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(FAIL_ON_IGNORED_PROPERTIES);

        DateFormat defaultDateFormat = new SimpleDateFormat(DATE_FORMAT);
        objectMapper.setDateFormat(defaultDateFormat);

        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addSerializer(Instant.class, new InstantSerializer());
        module.addDeserializer(Instant.class, new InstantDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }


}
