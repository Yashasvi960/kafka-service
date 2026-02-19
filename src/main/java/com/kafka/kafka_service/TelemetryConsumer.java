package com.kafka.kafka_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TelemetryConsumer {

    @KafkaListener(topics = "telemetry", groupId = "telemetry-group")
    public void listen(Telemetry telemetry) {
        if(telemetry.getSpeed()>70) {
            throw new RuntimeException("Simulated failure");
        }
        System.out.println("Received: " + telemetry);
    }
}
