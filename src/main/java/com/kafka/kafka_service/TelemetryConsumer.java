package com.kafka.kafka_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TelemetryConsumer {

    @KafkaListener(topics = "telemetry", groupId = "telemetry-group")
    public void listen(Telemetry telemetry) {
        System.out.println("Received: " + telemetry);
    }
}
