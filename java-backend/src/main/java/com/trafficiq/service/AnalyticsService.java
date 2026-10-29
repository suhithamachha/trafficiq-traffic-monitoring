package com.trafficiq.service;

import com.trafficiq.dto.RoadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AnalyticsService {

    @Autowired private RoadService roadService;

    public Map<String, Object> getAnalyticsData() {
        List<RoadDto> roads = roadService.getAllRoads();

        Map<String, Object> data = new HashMap<>();

        // Congestion breakdown
        Map<String, Integer> congestion = new LinkedHashMap<>();
        congestion.put("LOW", 0);
        congestion.put("MODERATE", 0);
        congestion.put("HEAVY", 0);
        congestion.put("SEVERE", 0);

        // Road Type breakdown
        Map<String, Integer> roadTypes = new LinkedHashMap<>();

        double totalDist = 0.0;
        int activeIncidents = 0;

        for (RoadDto r : roads) {
            String c = r.getCongestionLevel() != null ? r.getCongestionLevel() : "LOW";
            congestion.put(c, congestion.getOrDefault(c, 0) + 1);

            String t = r.getRoadType() != null ? r.getRoadType() : "ARTERIAL";
            roadTypes.put(t, roadTypes.getOrDefault(t, 0) + 1);

            totalDist += r.getDistanceKm().doubleValue();
            activeIncidents += (r.getActiveIncidents() != null ? r.getActiveIncidents() : 0);
        }

        // Top 5 congested roads sorted by lowest speed relative to speed limit
        List<RoadDto> congestedRoads = new ArrayList<>(roads);
        congestedRoads.sort((a, b) -> {
            double ratioA = a.getCurrentSpeedKmh().doubleValue() / a.getSpeedLimitKmh();
            double ratioB = b.getCurrentSpeedKmh().doubleValue() / b.getSpeedLimitKmh();
            return Double.compare(ratioA, ratioB);
        });

        List<Map<String, Object>> topCongested = new ArrayList<>();
        for (int i = 0; i < Math.min(5, congestedRoads.size()); i++) {
            RoadDto r = congestedRoads.get(i);
            topCongested.add(Map.of(
                    "road_name", r.getRoadName(),
                    "road_code", r.getRoadCode(),
                    "congestion", r.getCongestionLevel(),
                    "current_speed", r.getCurrentSpeedKmh(),
                    "speed_limit", r.getSpeedLimitKmh(),
                    "delay_minutes", r.getEstimatedTravelTimeMin()
            ));
        }

        // Hourly congestion trend (simulated 24-hour cycle)
        List<String> hours = Arrays.asList("06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00");
        List<Integer> avgSpeeds = Arrays.asList(55, 28, 22, 38, 42, 30, 18, 24, 48); // Rush hours at 10 AM and 6 PM
        List<Integer> incidentCounts = Arrays.asList(0, 2, 4, 1, 1, 3, 5, 3, 1);

        data.put("total_roads", roads.size());
        data.put("total_network_km", Math.round(totalDist * 10.0) / 10.0);
        data.put("active_incidents", activeIncidents);
        data.put("congestion_breakdown", congestion);
        data.put("road_type_breakdown", roadTypes);
        data.put("top_congested_corridors", topCongested);
        data.put("hourly_trend", Map.of(
                "labels", hours,
                "speeds", avgSpeeds,
                "incidents", incidentCounts
        ));
        data.put("is_simulated", true);

        return data;
    }

    public String generateCsvReport() {
        List<RoadDto> roads = roadService.getAllRoads();
        StringBuilder sb = new StringBuilder();
        sb.append("Road ID,Road Code,Road Name,Road Type,Distance (km),Speed Limit (km/h),Congestion Level,Current Speed (km/h),Est Travel Time (min),Active Incidents\n");

        for (RoadDto r : roads) {
            sb.append(r.getRoadId()).append(",")
              .append("\"").append(r.getRoadCode()).append("\",")
              .append("\"").append(r.getRoadName()).append("\",")
              .append(r.getRoadType()).append(",")
              .append(r.getDistanceKm()).append(",")
              .append(r.getSpeedLimitKmh()).append(",")
              .append(r.getCongestionLevel()).append(",")
              .append(r.getCurrentSpeedKmh()).append(",")
              .append(r.getEstimatedTravelTimeMin()).append(",")
              .append(r.getActiveIncidents() != null ? r.getActiveIncidents() : 0)
              .append("\n");
        }
        return sb.toString();
    }
}
