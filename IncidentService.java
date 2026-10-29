package com.trafficiq.service;

import com.trafficiq.dto.CreateIncidentRequest;
import com.trafficiq.dto.IncidentDto;
import com.trafficiq.dto.UpdateIncidentRequest;
import com.trafficiq.entity.*;
import com.trafficiq.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    @Autowired private IncidentRepository incidentRepository;
    @Autowired private RoadRepository roadRepository;
    @Autowired private TrafficSnapshotRepository snapshotRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private AuthService authService;

    @Transactional(readOnly = true)
    public List<IncidentDto> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentDto> getActiveIncidents() {
        return incidentRepository.findByStatusInOrderByReportedAtDesc(
                Arrays.asList(TrafficIncident.Status.REPORTED, TrafficIncident.Status.IN_PROGRESS)
        ).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentDto getIncidentById(Long id) {
        TrafficIncident inc = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident with ID " + id + " not found."));
        return mapToDto(inc);
    }

    @Transactional
    public IncidentDto createIncident(CreateIncidentRequest req) {
        Road road = roadRepository.findById(req.getRoadId())
                .orElseThrow(() -> new IllegalArgumentException("Road ID not found: " + req.getRoadId()));

        TrafficIncident incident = new TrafficIncident();
        incident.setRoad(road);
        incident.setIncidentType(TrafficIncident.IncidentType.valueOf(req.getIncidentType().toUpperCase()));
        incident.setSeverity(TrafficIncident.Severity.valueOf(req.getSeverity().toUpperCase()));
        incident.setDescription(req.getDescription());
        incident.setStatus(TrafficIncident.Status.REPORTED);
        incident.setImpactDelayMinutes(req.getImpactDelayMinutes() != null ? req.getImpactDelayMinutes() : 15);
        incident.setReportedAt(LocalDateTime.now());

        User currentUser = authService.getCurrentAuthenticatedUser().orElse(null);
        incident.setReportedByUser(currentUser);

        TrafficIncident saved = incidentRepository.save(incident);

        // Adjust road traffic snapshot to reflect incident bottleneck
        TrafficSnapshot.CongestionLevel newLevel = (saved.getSeverity() == TrafficIncident.Severity.CRITICAL) ?
                TrafficSnapshot.CongestionLevel.SEVERE :
                (saved.getSeverity() == TrafficIncident.Severity.HIGH) ?
                TrafficSnapshot.CongestionLevel.HEAVY : TrafficSnapshot.CongestionLevel.MODERATE;

        BigDecimal degradedSpeed = (newLevel == TrafficSnapshot.CongestionLevel.SEVERE) ?
                new BigDecimal("12.00") : new BigDecimal("20.00");

        TrafficSnapshot snap = new TrafficSnapshot();
        snap.setRoad(road);
        snap.setCongestionLevel(newLevel);
        snap.setAverageSpeedKmh(degradedSpeed);
        snap.setVehicleCount(750);
        BigDecimal travelTime = road.getDistanceKm()
                .divide(degradedSpeed, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("60"))
                .add(new BigDecimal(saved.getImpactDelayMinutes()))
                .setScale(2, RoundingMode.HALF_UP);
        snap.setEstimatedTravelTimeMin(travelTime);
        snap.setRecordedAt(LocalDateTime.now());
        snap.setDataSource(TrafficSnapshot.DataSource.DEMO_SIMULATED);
        snapshotRepository.save(snap);

        // Audit Log
        auditLogRepository.save(new AuditLog(
                currentUser, "CREATE_INCIDENT", "TRAFFIC_INCIDENTS", saved.getIncidentId(),
                "Reported " + saved.getSeverity() + " " + saved.getIncidentType() + " on " + road.getRoadName(),
                "127.0.0.1"
        ));

        return mapToDto(saved);
    }

    @Transactional
    public IncidentDto updateIncident(Long id, UpdateIncidentRequest req) {
        TrafficIncident inc = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident ID not found: " + id));

        if (req.getStatus() != null) {
            TrafficIncident.Status newStatus = TrafficIncident.Status.valueOf(req.getStatus().toUpperCase());
            if (inc.getStatus() != TrafficIncident.Status.RESOLVED && newStatus == TrafficIncident.Status.RESOLVED) {
                inc.setResolvedAt(LocalDateTime.now());

                // Restore road traffic condition
                TrafficSnapshot snap = new TrafficSnapshot();
                snap.setRoad(inc.getRoad());
                snap.setCongestionLevel(TrafficSnapshot.CongestionLevel.LOW);
                BigDecimal normalSpeed = new BigDecimal(inc.getRoad().getSpeedLimitKmh()).multiply(new BigDecimal("0.85"));
                snap.setAverageSpeedKmh(normalSpeed);
                snap.setVehicleCount(300);
                BigDecimal travelTime = inc.getRoad().getDistanceKm()
                        .divide(normalSpeed, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("60"))
                        .setScale(2, RoundingMode.HALF_UP);
                snap.setEstimatedTravelTimeMin(travelTime);
                snap.setRecordedAt(LocalDateTime.now());
                snap.setDataSource(TrafficSnapshot.DataSource.DEMO_SIMULATED);
                snapshotRepository.save(snap);
            }
            inc.setStatus(newStatus);
        }

        if (req.getSeverity() != null) {
            inc.setSeverity(TrafficIncident.Severity.valueOf(req.getSeverity().toUpperCase()));
        }
        if (req.getDescription() != null) {
            inc.setDescription(req.getDescription());
        }
        if (req.getImpactDelayMinutes() != null) {
            inc.setImpactDelayMinutes(req.getImpactDelayMinutes());
        }

        TrafficIncident updated = incidentRepository.save(inc);

        User currentUser = authService.getCurrentAuthenticatedUser().orElse(null);
        auditLogRepository.save(new AuditLog(
                currentUser, "UPDATE_INCIDENT", "TRAFFIC_INCIDENTS", updated.getIncidentId(),
                "Updated incident on " + inc.getRoad().getRoadName() + " (Status: " + updated.getStatus() + ")",
                "127.0.0.1"
        ));

        return mapToDto(updated);
    }

    @Transactional
    public void deleteIncident(Long id) {
        TrafficIncident inc = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident with ID " + id + " not found."));
        incidentRepository.delete(inc);

        User currentUser = authService.getCurrentAuthenticatedUser().orElse(null);
        auditLogRepository.save(new AuditLog(
                currentUser, "DELETE_INCIDENT", "TRAFFIC_INCIDENTS", id,
                "Deleted incident record from road: " + inc.getRoad().getRoadName(),
                "127.0.0.1"
        ));
    }

    public IncidentDto mapToDto(TrafficIncident inc) {
        IncidentDto dto = new IncidentDto();
        dto.setIncidentId(inc.getIncidentId());
        dto.setRoadId(inc.getRoad().getRoadId());
        dto.setRoadName(inc.getRoad().getRoadName());
        dto.setIncidentType(inc.getIncidentType().name());
        dto.setSeverity(inc.getSeverity().name());
        dto.setDescription(inc.getDescription());
        dto.setStatus(inc.getStatus().name());
        dto.setReportedByName(inc.getReportedByUser() != null ? inc.getReportedByUser().getName() : "Public Traffic Monitor");
        dto.setReportedAt(inc.getReportedAt());
        dto.setResolvedAt(inc.getResolvedAt());
        dto.setImpactDelayMinutes(inc.getImpactDelayMinutes());

        // Midpoint coordinates for map marker placement
        BigDecimal lat = inc.getRoad().getStartLocation().getLatitude()
                .add(inc.getRoad().getEndLocation().getLatitude())
                .divide(new BigDecimal("2"), 7, RoundingMode.HALF_UP);
        BigDecimal lng = inc.getRoad().getStartLocation().getLongitude()
                .add(inc.getRoad().getEndLocation().getLongitude())
                .divide(new BigDecimal("2"), 7, RoundingMode.HALF_UP);
        dto.setLatitude(lat);
        dto.setLongitude(lng);

        return dto;
    }
}
