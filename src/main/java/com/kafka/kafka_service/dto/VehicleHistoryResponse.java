package com.kafka.kafka_service.dto;

import java.util.ArrayList;
import java.util.List;

public class VehicleHistoryResponse {
    private String vehicleId;
    private List<HistoryEntry> history;

    public VehicleHistoryResponse(String vehicleId) {
        this.vehicleId = vehicleId;
        this.history = new ArrayList<>();
    }

    public void addEntry(String time, String action, Object data) {
        history.add(new HistoryEntry(time, action, data));
    }

    public String getVehicleId() { return vehicleId; }
    public List<HistoryEntry> getHistory() { return history; }

    public static class HistoryEntry {
        private String time;
        private String action;
        private Object data;

        public HistoryEntry(String time, String action, Object data) {
            this.time = time;
            this.action = action;
            this.data = data;
        }

        public String getTime() { return time; }
        public String getAction() { return action; }
        public Object getData() { return data; }
    }
}

