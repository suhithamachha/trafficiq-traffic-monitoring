package com.trafficiq.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_incidents")
public class TrafficIncident {

    public enum IncidentType {
        ACCIDENT, ROAD_CLOSURE, CONSTRUCTION, WATERLOGGING, BREAKDOWN, PROTEST
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Status {
        REPORTED, IN_PROGRESS, RESOLVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incident_id")
    private Long incidentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "road_id", nullable = false)
    private Road road;

    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", nullable = false, length = 30)
    private IncidentType incidentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity = Severity.MEDIUM;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.REPORTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_user_id")
    private User reportedByUser;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "impact_delay_minutes", nullable = false)
    private Integer impactDelayMinutes = 15;

    public TrafficIncident() {}

    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public Road getRoad() { return road; }
    public void setRoad(Road road) { this.road = road; }

    public IncidentType getIncidentType() { return incidentType; }
    public void setIncidentType(IncidentType incidentType) { this.incidentType = incidentType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getReportedByUser() { return reportedByUser; }
    public void setReportedByUser(User reportedByUser) { this.reportedByUser = reportedByUser; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public Integer getImpactDelayMinutes() { return impactDelayMinutes; }
    public void setImpactDelayMinutes(Integer impactDelayMinutes) { this.impactDelayMinutes = impactDelayMinutes; }
}
