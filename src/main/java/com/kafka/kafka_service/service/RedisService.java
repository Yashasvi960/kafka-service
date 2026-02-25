package com.kafka.kafka_service.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper mapper;

    public RedisService(RedisTemplate<String, String> redisTemplate, ObjectMapper mapper) {
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T> T get(String vehicleId, Class<T> telemetryClass) {
        Object o = redisTemplate.opsForValue().get(vehicleId);

        if(o==null) {
            return null;
        }

        try {
            return mapper.readValue(String.valueOf(o), telemetryClass);
        } catch (Exception e) {
            log.error("Error deserializing telemetry data for vehicle {}: {}", vehicleId, e.getMessage(), e);
            return null;
        }

    }

    public <T> void set(String vehicleId, Object telemetry) {
        try {
            String json = mapper.writeValueAsString(telemetry);
            redisTemplate.opsForValue().set(vehicleId, json);
        } catch (Exception e) {
            log.error("Error serializing telemetry data for vehicle {}: {}", vehicleId, e.getMessage(), e);
        }
    }
}
