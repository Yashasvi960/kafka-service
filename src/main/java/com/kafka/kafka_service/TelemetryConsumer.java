package com.kafka.kafka_service;

import com.kafka.kafka_service.service.RedisService;
import com.kafka.kafka_service.service.TelemetryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TelemetryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryConsumer.class);
    private final TelemetryService telemetryService;
    private final RedisService redisService;

    public TelemetryConsumer(TelemetryService telemetryService, RedisService redisService) {
        this.telemetryService = telemetryService;
        this.redisService = redisService;
    }

    @KafkaListener(topics = "telemetry", groupId = "telemetry-group")
    public void listen(Telemetry telemetry) {
        // Update the service with latest telemetry
        telemetryService.updateTelemetry(telemetry);
        if (telemetry.getSpeed() > 60) {
            logger.warn("Telemetry over speed limit; sending to DLT for vehicle {}", telemetry.getVehicleId());
            throw new IllegalArgumentException("Speed limit exceeded");
        }

        Object o = redisService.get(telemetry.getVehicleId(), Telemetry.class);
        if(o!=null) {
            logger.info("Telemetry for vehicle {} retrieved from Redis: {}", telemetry.getVehicleId(), o);
        } else {
            redisService.set(telemetry.getVehicleId(), telemetry);
            logger.info("Received telemetry for vehicle {}: speed={}, location=({}, {})",
                    telemetry.getVehicleId(), telemetry.getSpeed(), telemetry.getLat(), telemetry.getLon());

            System.out.println("Received: " + telemetry);

        }
    }
}
