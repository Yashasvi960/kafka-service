package com.kafka.kafka_service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Profile("!test")
public class TelemetryProducer {
    private static final Logger logger = LoggerFactory.getLogger(TelemetryProducer.class);
    private final KafkaTemplate<String, Telemetry> kafkaTemplate;
    private final Random rnd = new Random();
    private ScheduledExecutorService executor;

    public TelemetryProducer(KafkaTemplate<String, Telemetry> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void start() {
        logger.info("Starting TelemetryProducer...");
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "TelemetryProducer-Thread");
            t.setDaemon(false);
            return t;
        });
        executor.scheduleAtFixedRate(this::sendBatch, 0, 5, TimeUnit.MINUTES);
    }

    private void sendBatch() {
        logger.debug("Sending batch of telemetry data...");
        int vehicle = 50;
        for (int i = 1; i <= vehicle; i++) {
            try {
                Telemetry t = new Telemetry();
                t.setVehicleId("veh-" + i);
                t.setTimestamp(Instant.now());
                t.setLat(40.0 + rnd.nextDouble() * 0.1);
                t.setLon(-73.9 + rnd.nextDouble() * 0.1);
                t.setSpeed(Math.round(rnd.nextDouble() * 80 * 100.0) / 100.0);

                kafkaTemplate.send("telemetry", t.getVehicleId(), t).whenComplete((result, ex) -> {
                    if (ex != null) {
                        logger.error("Failed to send telemetry for vehicle {}: {}", t.getVehicleId(), ex.getMessage(), ex);
                    } else {
                        logger.info("Successfully sent telemetry for vehicle {} to partition {} with offset {}",
                                t.getVehicleId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
            } catch (Exception e) {
                logger.error("Error sending telemetry data: {}", e.getMessage(), e);
            }
        }
    }
}
