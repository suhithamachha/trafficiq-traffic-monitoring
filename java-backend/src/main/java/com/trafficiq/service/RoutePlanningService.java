package com.trafficiq.service;

import com.trafficiq.dto.RoadDto;
import com.trafficiq.dto.RoutePlanRequest;
import com.trafficiq.entity.Location;
import com.trafficiq.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class RoutePlanningService {

    private static final Logger log = LoggerFactory.getLogger(RoutePlanningService.class);

    @Autowired private RoadService roadService;
    @Autowired private LocationRepository locationRepository;
    @Autowired private RestTemplate restTemplate;

    @Value("${python.service.url:http://localhost:8000}")
    private String pythonServiceUrl;

    public Map<String, Object> planRoute(RoutePlanRequest req) {
        Location origin = locationRepository.findById(req.getOriginLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Origin location not found."));
        Location dest = locationRepository.findById(req.getDestinationLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Destination location not found."));

        if (origin.getLocationId().equals(dest.getLocationId())) {
            throw new IllegalArgumentException("Origin and Destination must be distinct locations.");
        }

        List<RoadDto> liveRoads = roadService.getAllRoads();

        // 1. Try communicating with Python FastAPI Intelligence Service
        try {
            String endpoint = pythonServiceUrl + "/optimize-route";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("origin_location_id", req.getOriginLocationId());
            requestBody.put("destination_location_id", req.getDestinationLocationId());
            requestBody.put("preference", req.getPreference());
            requestBody.put("transport_mode", req.getTransportMode());
            requestBody.put("avoid_tolls", req.getAvoidTolls());

            // Pass live database road conditions to Python
            List<Map<String, Object>> customRoads = new ArrayList<>();
            for (RoadDto r : liveRoads) {
                Map<String, Object> rMap = new HashMap<>();
                rMap.put("road_id", r.getRoadId());
                rMap.put("road_name", r.getRoadName());
                rMap.put("road_code", r.getRoadCode());
                rMap.put("start_location_id", r.getStartLocationId());
                rMap.put("end_location_id", r.getEndLocationId());
                rMap.put("distance_km", r.getDistanceKm());
                rMap.put("speed_limit_kmh", r.getSpeedLimitKmh());
                rMap.put("congestion_level", r.getCongestionLevel());
                rMap.put("road_type", r.getRoadType());
                rMap.put("active_incidents", r.getActiveIncidents());
                rMap.put("impact_delay_minutes", r.getActiveIncidents() > 0 ? 20 : 0);
                rMap.put("is_bidirectional", r.getIsBidirectional());
                customRoads.add(rMap);
            }
            requestBody.put("custom_roads", customRoads);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                result.put("source", "PYTHON_FASTAPI_SERVICE");
                return result;
            }
        } catch (Exception e) {
            log.warn("Python intelligence service at {} unreachable ({}). Falling back to Java graph routing engine.",
                    pythonServiceUrl, e.getMessage());
        }

        // 2. Resilient Fallback: Java Graph Router
        return computeJavaFallbackRoute(origin, dest, req.getPreference(), liveRoads);
    }

    private Map<String, Object> computeJavaFallbackRoute(
            Location origin, Location dest, String preference, List<RoadDto> roads) {

        // Build adjacency map: node -> list of (neighbor_id, RoadDto)
        Map<Long, List<RoadEdgePair>> adj = new HashMap<>();
        for (RoadDto r : roads) {
            adj.computeIfAbsent(r.getStartLocationId(), k -> new ArrayList<>())
                    .add(new RoadEdgePair(r.getEndLocationId(), r));
            if (Boolean.TRUE.equals(r.getIsBidirectional())) {
                adj.computeIfAbsent(r.getEndLocationId(), k -> new ArrayList<>())
                        .add(new RoadEdgePair(r.getStartLocationId(), r));
            }
        }

        // Priority queue for Dijkstra: min cost
        PriorityQueue<RouteNodeState> pq = new PriorityQueue<>(Comparator.comparingDouble(s -> s.cost));
        Map<Long, Double> minCosts = new HashMap<>();

        pq.add(new RouteNodeState(origin.getLocationId(), 0.0, Collections.singletonList(origin.getLocationId()), new ArrayList<>()));
        minCosts.put(origin.getLocationId(), 0.0);

        RouteNodeState targetState = null;

        while (!pq.isEmpty()) {
            RouteNodeState current = pq.poll();
            if (current.nodeId.equals(dest.getLocationId())) {
                targetState = current;
                break;
            }

            if (current.cost > minCosts.getOrDefault(current.nodeId, Double.MAX_VALUE)) {
                continue;
            }

            List<RoadEdgePair> neighbors = adj.getOrDefault(current.nodeId, Collections.emptyList());
            for (RoadEdgePair pair : neighbors) {
                double edgeWeight = calculateEdgeWeight(pair.road, preference);
                double nextCost = current.cost + edgeWeight;

                if (nextCost < minCosts.getOrDefault(pair.targetNodeId, Double.MAX_VALUE)) {
                    minCosts.put(pair.targetNodeId, nextCost);
                    List<Long> nextPath = new ArrayList<>(current.path);
                    nextPath.add(pair.targetNodeId);
                    List<RoadDto> nextRoads = new ArrayList<>(current.roads);
                    nextRoads.add(pair.road);
                    pq.add(new RouteNodeState(pair.targetNodeId, nextCost, nextPath, nextRoads));
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("origin_location", Map.of(
                "location_id", origin.getLocationId(),
                "location_name", origin.getLocationName(),
                "latitude", origin.getLatitude(),
                "longitude", origin.getLongitude()
        ));
        response.put("destination_location", Map.of(
                "location_id", dest.getLocationId(),
                "location_name", dest.getLocationName(),
                "latitude", dest.getLatitude(),
                "longitude", dest.getLongitude()
        ));
        response.put("is_simulated_data", true);
        response.put("source", "JAVA_INTERNAL_FALLBACK");

        if (targetState != null) {
            double totalDist = 0;
            double totalTime = 0;
            List<Map<String, Object>> segments = new ArrayList<>();

            for (int i = 0; i < targetState.roads.size(); i++) {
                RoadDto r = targetState.roads.get(i);
                totalDist += r.getDistanceKm().doubleValue();
                double estTime = r.getEstimatedTravelTimeMin() != null ?
                        r.getEstimatedTravelTimeMin().doubleValue() : (r.getDistanceKm().doubleValue() / 40.0) * 60;
                totalTime += estTime;

                Map<String, Object> seg = new HashMap<>();
                seg.put("sequence", i + 1);
                seg.put("road_id", r.getRoadId());
                seg.put("road_name", r.getRoadName());
                seg.put("start_location_name", r.getStartLocationName());
                seg.put("end_location_name", r.getEndLocationName());
                seg.put("distance_km", r.getDistanceKm());
                seg.put("duration_minutes", Math.round(estTime * 10.0) / 10.0);
                seg.put("congestion_level", r.getCongestionLevel());
                seg.put("coordinates", Arrays.asList(
                        Arrays.asList(r.getStartLatitude(), r.getStartLongitude()),
                        Arrays.asList(r.getEndLatitude(), r.getEndLongitude())
                ));
                segments.add(seg);
            }

            Map<String, Object> primary = new HashMap<>();
            primary.put("route_name", "Primary Optimal Route (Dijkstra)");
            primary.put("preference", preference);
            primary.put("total_distance_km", Math.round(totalDist * 100.0) / 100.0);
            primary.put("estimated_duration_minutes", Math.round(totalTime * 10.0) / 10.0);
            primary.put("route_score", 88.5);
            primary.put("algorithm_used", "DIJKSTRA");
            primary.put("segments", segments);
            primary.put("path_node_ids", targetState.path);
            primary.put("summary_advisory", "Route computed via Java fallback Dijkstra algorithm across " + segments.size() + " road segments.");

            response.put("primary_route", primary);
            response.put("alternative_routes", Collections.emptyList());
            response.put("advisory_notes", "Simulated traffic route calculation.");
        } else {
            response.put("primary_route", null);
            response.put("alternative_routes", Collections.emptyList());
            response.put("advisory_notes", "No viable road path found between selected locations.");
        }

        return response;
    }

    private double calculateEdgeWeight(RoadDto r, String preference) {
        double dist = r.getDistanceKm().doubleValue();
        if ("SHORTEST".equalsIgnoreCase(preference)) {
            return dist;
        }

        double speedFactor = "SEVERE".equalsIgnoreCase(r.getCongestionLevel()) ? 0.20 :
                "HEAVY".equalsIgnoreCase(r.getCongestionLevel()) ? 0.40 :
                "MODERATE".equalsIgnoreCase(r.getCongestionLevel()) ? 0.70 : 1.00;

        double speed = Math.max(10.0, r.getSpeedLimitKmh() * speedFactor);
        double durationMin = (dist / speed) * 60.0;
        if (r.getActiveIncidents() != null && r.getActiveIncidents() > 0) {
            durationMin += 15.0;
        }

        if ("FASTEST".equalsIgnoreCase(preference)) {
            return durationMin;
        }
        return (durationMin * 0.6) + (dist * 0.4);
    }

    private static class RoadEdgePair {
        Long targetNodeId;
        RoadDto road;
        RoadEdgePair(Long targetNodeId, RoadDto road) {
            this.targetNodeId = targetNodeId;
            this.road = road;
        }
    }

    private static class RouteNodeState {
        Long nodeId;
        double cost;
        List<Long> path;
        List<RoadDto> roads;
        RouteNodeState(Long nodeId, double cost, List<Long> path, List<RoadDto> roads) {
            this.nodeId = nodeId;
            this.cost = cost;
            this.path = path;
            this.roads = roads;
        }
    }
}
