package com.kafka.kafka_service;

import java.time.Instant;

public class Telemetry {
    private String vehicleId;
    private Instant timestamp;

    private double lat;

    private double lon;

    private double speed;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Telemetry{" + "vehicleId='" + vehicleId + '\'' + ", ts=" + timestamp + ", lat=" + lat + ", lon=" + lon + ", speed=" + speed + '}';
    }
}
