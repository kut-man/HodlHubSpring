package com.example.hodlhub.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    // Enable pretty printing of JSON
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    // Configure Jackson to ignore null values globally
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.registerModule(new JavaTimeModule());

    objectMapper
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .setDateFormat(new StdDateFormat())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    objectMapper
        .configOverride(Map.class)
        .setInclude(JsonInclude.Value.construct(Include.NON_NULL, Include.NON_NULL));

    return objectMapper;
  }
}
