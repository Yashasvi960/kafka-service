package com.kafka.kafka_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TelemetryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryConsumer.class);

    @KafkaListener(topics = "telemetry", groupId = "telemetry-group")
    public void listen(Telemetry telemetry) {
        if (telemetry.getSpeed() > 60) {
            logger.warn("Telemetry over speed limit; sending to DLT for vehicle {}", telemetry.getVehicleId());
            throw new IllegalArgumentException("Speed limit exceeded");
        }

        logger.info("Received telemetry for vehicle {}: speed={}, location=({}, {})",
                telemetry.getVehicleId(), telemetry.getSpeed(), telemetry.getLat(), telemetry.getLon());

        System.out.println("Received: " + telemetry);
    }
}
