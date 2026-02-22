package com.kafka.kafka_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class Telemetry {
    private String vehicleId;
    private Instant timestamp;
    private double lat;
    private double lon;
    private double speed;

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public Instant getTimestamp() { return timestamp; }
    // keep a normal setter in case other code sets an Instant
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    // Accept numeric seconds (e.g. 1771536540.346823) and convert to Instant
    @JsonProperty("timestamp")
    public void setTimestampFromDouble(Number tsSeconds) {
        if (tsSeconds == null) {
            this.timestamp = null;
            return;
        }
        // convert seconds (may be fractional) -> millis
        double seconds = tsSeconds.doubleValue();
        long millis = (long) (seconds * 1000.0);
        this.timestamp = Instant.ofEpochMilli(millis);
    }

    // Also accept string representations just in case
    @JsonProperty("timestamp")
    public void setTimestampFromString(String ts) {
        if (ts == null || ts.isBlank()) {
            this.timestamp = null;
            return;
        }
        try {
            double seconds = Double.parseDouble(ts);
            long millis = (long) (seconds * 1000.0);
            this.timestamp = Instant.ofEpochMilli(millis);
        } catch (NumberFormatException ex) {
            // fallback: try parsing as ISO date/time
            this.timestamp = Instant.parse(ts);
        }
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    @Override
    public String toString() {
        return "Telemetry{" + "vehicleId='" + vehicleId + '\'' + ", ts=" + timestamp + ", lat=" + lat + ", lon=" + lon + ", speed=" + speed + '}';
    }
}