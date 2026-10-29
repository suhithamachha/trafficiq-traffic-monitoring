package com.trafficiq.service;

import com.trafficiq.dto.CreateRoadRequest;
import com.trafficiq.dto.RoadDto;
import com.trafficiq.entity.*;
import com.trafficiq.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadService {

    @Autowired private RoadRepository roadRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private TrafficSnapshotRepository snapshotRepository;
    @Autowired private IncidentRepository incidentRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private AuthService authService;

    @Transactional(readOnly = true)
    public List<RoadDto> getAllRoads() {
        List<Road> roads = roadRepository.findAll();
        return roads.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoadDto getRoadById(Long id) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Road with ID " + id + " not found."));
        return mapToDto(road);
    }

    @Transactional
    public RoadDto createRoad(CreateRoadRequest req) {
        if (req.getStartLocationId().equals(req.getEndLocationId())) {
            throw new IllegalArgumentException("Start and End locations must be distinct.");
        }

        Location start = locationRepository.findById(req.getStartLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Start location ID not found."));
        Location end = locationRepository.findById(req.getEndLocationId())
                .orElseThrow(() -> new IllegalArgumentException("End location ID not found."));

        Road road = new Road();
        road.setRoadName(req.getRoadName());
        road.setRoadCode(req.getRoadCode());
        road.setStartLocation(start);
        road.setEndLocation(end);
        road.setRoadType(Road.RoadType.valueOf(req.getRoadType().toUpperCase()));
        road.setDistanceKm(req.getDistanceKm());
        road.setSpeedLimitKmh(req.getSpeedLimitKmh());
        road.setLanes(req.getLanes() != null ? req.getLanes() : 4);
        road.setIsToll(req.getIsToll() != null ? req.getIsToll() : false);
        road.setIsBidirectional(req.getIsBidirectional() != null ? req.getIsBidirectional() : true);

        Road saved = roadRepository.save(road);

        // Create baseline snapshot
        TrafficSnapshot snap = new TrafficSnapshot();
        snap.setRoad(saved);
        snap.setCongestionLevel(TrafficSnapshot.CongestionLevel.LOW);
        snap.setAverageSpeedKmh(new BigDecimal(saved.getSpeedLimitKmh()));
        snap.setVehicleCount(100);
        BigDecimal travelTime = saved.getDistanceKm()
                .divide(new BigDecimal(saved.getSpeedLimitKmh()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("60"))
                .setScale(2, RoundingMode.HALF_UP);
        snap.setEstimatedTravelTimeMin(travelTime);
        snap.setRecordedAt(LocalDateTime.now());
        snap.setDataSource(TrafficSnapshot.DataSource.DEMO_SIMULATED);
        snapshotRepository.save(snap);

        // Audit Log
        User currentUser = authService.getCurrentAuthenticatedUser().orElse(null);
        auditLogRepository.save(new AuditLog(
                currentUser, "CREATE_ROAD", "ROADS", saved.getRoadId(),
                "Added new road: " + saved.getRoadName() + " (" + saved.getRoadCode() + ")", "127.0.0.1"
        ));

        return mapToDto(saved);
    }

    @Transactional
    public RoadDto updateRoad(Long id, CreateRoadRequest req) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Road with ID " + id + " not found."));

        road.setRoadName(req.getRoadName());
        road.setDistanceKm(req.getDistanceKm());
        road.setSpeedLimitKmh(req.getSpeedLimitKmh());
        road.setLanes(req.getLanes() != null ? req.getLanes() : road.getLanes());
        road.setIsToll(req.getIsToll() != null ? req.getIsToll() : road.getIsToll());
        road.setIsBidirectional(req.getIsBidirectional() != null ? req.getIsBidirectional() : road.getIsBidirectional());

        Road updated = roadRepository.save(road);

        User currentUser = authService.getCurrentAuthenticatedUser().orElse(null);
        auditLogRepository.save(new AuditLog(
                currentUser, "UPDATE_ROAD", "ROADS", updated.getRoadId(),
                "Updated road properties: " + updated.getRoadName(), "127.0.0.1"
        ));

        return mapToDto(updated);
    }

    @Transactional
    public void deleteRoad(Long id) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Road with ID " + id + " not found."));
        roadRepository.delete(road);

        User currentUser = authService.getCurrentAuthenticatedUser().orElse(null);
        auditLogRepository.save(new AuditLog(
                currentUser, "DELETE_ROAD", "ROADS", id,
                "Deleted road segment: " + road.getRoadName(), "127.0.0.1"
        ));
    }

    public RoadDto mapToDto(Road road) {
        RoadDto dto = new RoadDto();
        dto.setRoadId(road.getRoadId());
        dto.setRoadName(road.getRoadName());
        dto.setRoadCode(road.getRoadCode());
        dto.setStartLocationId(road.getStartLocation().getLocationId());
        dto.setStartLocationName(road.getStartLocation().getLocationName());
        dto.setStartLatitude(road.getStartLocation().getLatitude());
        dto.setStartLongitude(road.getStartLocation().getLongitude());
        dto.setEndLocationId(road.getEndLocation().getLocationId());
        dto.setEndLocationName(road.getEndLocation().getLocationName());
        dto.setEndLatitude(road.getEndLocation().getLatitude());
        dto.setEndLongitude(road.getEndLocation().getLongitude());
        dto.setRoadType(road.getRoadType().name());
        dto.setDistanceKm(road.getDistanceKm());
        dto.setSpeedLimitKmh(road.getSpeedLimitKmh());
        dto.setLanes(road.getLanes());
        dto.setIsToll(road.getIsToll());
        dto.setIsBidirectional(road.getIsBidirectional());

        // Fetch latest snapshot
        snapshotRepository.findTopByRoadOrderByRecordedAtDesc(road).ifPresentOrElse(
                snap -> {
                    dto.setCongestionLevel(snap.getCongestionLevel().name());
                    dto.setCurrentSpeedKmh(snap.getAverageSpeedKmh());
                    dto.setEstimatedTravelTimeMin(snap.getEstimatedTravelTimeMin());
                },
                () -> {
                    dto.setCongestionLevel("LOW");
                    dto.setCurrentSpeedKmh(new BigDecimal(road.getSpeedLimitKmh()));
                    BigDecimal travelTime = road.getDistanceKm()
                            .divide(new BigDecimal(road.getSpeedLimitKmh()), 2, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("60"));
                    dto.setEstimatedTravelTimeMin(travelTime);
                }
        );

        List<TrafficIncident> incidents = incidentRepository.findByRoadRoadId(road.getRoadId());
        long activeCount = incidents.stream()
                .filter(i -> i.getStatus() != TrafficIncident.Status.RESOLVED)
                .count();
        dto.setActiveIncidents((int) activeCount);

        return dto;
    }
}
