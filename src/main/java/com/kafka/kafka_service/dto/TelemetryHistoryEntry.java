package com.kafka.kafka_service.dto;

import com.kafka.kafka_service.Telemetry;
import java.time.Instant;

public class TelemetryHistoryEntry {
    private Instant receivedAt;
    private String vehicleId;
    private double speed;
    private double lat;
    private double lon;
    private Instant originalTimestamp;

    public TelemetryHistoryEntry(Telemetry telemetry) {
        this.receivedAt = Instant.now();
        this.vehicleId = telemetry.getVehicleId();
        this.speed = telemetry.getSpeed();
        this.lat = telemetry.getLat();
        this.lon = telemetry.getLon();
        this.originalTimestamp = telemetry.getTimestamp();
    }

    // Getters
    public Instant getReceivedAt() { return receivedAt; }
    public String getVehicleId() { return vehicleId; }
    public double getSpeed() { return speed; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public Instant getOriginalTimestamp() { return originalTimestamp; }
}

