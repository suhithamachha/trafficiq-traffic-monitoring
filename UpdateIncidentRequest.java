package com.trafficiq.dto;

public class UpdateIncidentRequest {
    private String status;
    private String severity;
    private String description;
    private Integer impactDelayMinutes;

    public UpdateIncidentRequest() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getImpactDelayMinutes() { return impactDelayMinutes; }
    public void setImpactDelayMinutes(Integer impactDelayMinutes) { this.impactDelayMinutes = impactDelayMinutes; }
}
