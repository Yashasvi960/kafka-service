package com.kafka.kafka_service.Controller;

import com.kafka.kafka_service.Telemetry;
import com.kafka.kafka_service.dto.TelemetryHistoryEntry;
import com.kafka.kafka_service.dto.VehicleHistoryResponse;
import com.kafka.kafka_service.service.TelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping("/vehicles")
   public ResponseEntity<List<Telemetry>> getAllVehicles() {
        List<Telemetry> vehicles = telemetryService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/vehicle/{id}")
    public ResponseEntity<?> getVehicleTelemetry(@PathVariable String id) {
        Telemetry telemetry = telemetryService.getVehicleTelemetry(id);
        if (telemetry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(telemetry);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVehicles", telemetryService.getVehicleCount());
        stats.put("activeVehicles", telemetryService.getAllVehicles().size());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/vehicle/{id}/history")
    public ResponseEntity<?> getVehicleHistory(@PathVariable String id) {
        List<TelemetryHistoryEntry> history = telemetryService.getVehicleHistory(id);
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        VehicleHistoryResponse response = new VehicleHistoryResponse(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (TelemetryHistoryEntry entry : history) {
            String time = entry.getReceivedAt().toString().substring(11, 19); // Extract HH:mm:ss
            String action = "Consumer stores/updates";
            Map<String, Object> data = new HashMap<>();
            data.put(id, Map.of(
                "speed", entry.getSpeed(),
                "lat", entry.getLat(),
                "lon", entry.getLon(),
                "timestamp", entry.getOriginalTimestamp()
            ));
            response.addEntry(time, action, data);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicles/history")
    public ResponseEntity<Map<String, Object>> getAllVehiclesWithHistory() {
        Map<String, Object> response = new HashMap<>();
        Map<String, List<TelemetryHistoryEntry>> allHistory = telemetryService.getAllHistory();

        for (Map.Entry<String, List<TelemetryHistoryEntry>> entry : allHistory.entrySet()) {
            String vehicleId = entry.getKey();
            List<TelemetryHistoryEntry> history = entry.getValue();

            if (!history.isEmpty()) {
                TelemetryHistoryEntry latest = history.get(history.size() - 1);
                Map<String, Object> vehicleData = new HashMap<>();
                vehicleData.put("currentSpeed", latest.getSpeed());
                vehicleData.put("currentLat", latest.getLat());
                vehicleData.put("currentLon", latest.getLon());
                vehicleData.put("lastUpdate", latest.getReceivedAt());
                vehicleData.put("historyCount", history.size());
                response.put(vehicleId, vehicleData);
            }
        }

        return ResponseEntity.ok(response);
    }
}
