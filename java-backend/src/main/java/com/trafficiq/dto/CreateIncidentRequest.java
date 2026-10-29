package com.trafficiq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateIncidentRequest {

    @NotNull(message = "Road ID is required")
    private Long roadId;

    @NotBlank(message = "Incident type is required")
    private String incidentType;

    @NotBlank(message = "Severity is required")
    private String severity;

    @NotBlank(message = "Description is required")
    private String description;

    private Integer impactDelayMinutes = 15;

    public CreateIncidentRequest() {}

    public Long getRoadId() { return roadId; }
    public void setRoadId(Long roadId) { this.roadId = roadId; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getImpactDelayMinutes() { return impactDelayMinutes; }
    public void setImpactDelayMinutes(Integer impactDelayMinutes) { this.impactDelayMinutes = impactDelayMinutes; }
}
