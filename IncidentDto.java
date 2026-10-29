package com.trafficiq.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IncidentDto {
    private Long incidentId;
    private Long roadId;
    private String roadName;
    private String incidentType;
    private String severity;
    private String description;
    private String status;
    private String reportedByName;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private Integer impactDelayMinutes;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public IncidentDto() {}

    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public Long getRoadId() { return roadId; }
    public void setRoadId(Long roadId) { this.roadId = roadId; }

    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReportedByName() { return reportedByName; }
    public void setReportedByName(String reportedByName) { this.reportedByName = reportedByName; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public Integer getImpactDelayMinutes() { return impactDelayMinutes; }
    public void setImpactDelayMinutes(Integer impactDelayMinutes) { this.impactDelayMinutes = impactDelayMinutes; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
}
