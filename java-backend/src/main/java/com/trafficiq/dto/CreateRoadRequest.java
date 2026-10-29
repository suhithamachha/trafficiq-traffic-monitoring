package com.trafficiq.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateRoadRequest {

    @NotBlank(message = "Road name is required")
    private String roadName;

    @NotBlank(message = "Road code is required")
    private String roadCode;

    @NotNull(message = "Start location ID is required")
    private Long startLocationId;

    @NotNull(message = "End location ID is required")
    private Long endLocationId;

    private String roadType = "ARTERIAL";

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.1", message = "Distance must be at least 0.1 km")
    private BigDecimal distanceKm;

    @NotNull(message = "Speed limit is required")
    @Min(value = 20, message = "Speed limit must be at least 20 km/h")
    @Max(value = 140, message = "Speed limit cannot exceed 140 km/h")
    private Integer speedLimitKmh = 50;

    private Integer lanes = 4;
    private Boolean isToll = false;
    private Boolean isBidirectional = true;

    public CreateRoadRequest() {}

    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }

    public String getRoadCode() { return roadCode; }
    public void setRoadCode(String roadCode) { this.roadCode = roadCode; }

    public Long getStartLocationId() { return startLocationId; }
    public void setStartLocationId(Long startLocationId) { this.startLocationId = startLocationId; }

    public Long getEndLocationId() { return endLocationId; }
    public void setEndLocationId(Long endLocationId) { this.endLocationId = endLocationId; }

    public String getRoadType() { return roadType; }
    public void setRoadType(String roadType) { this.roadType = roadType; }

    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

    public Integer getSpeedLimitKmh() { return speedLimitKmh; }
    public void setSpeedLimitKmh(Integer speedLimitKmh) { this.speedLimitKmh = speedLimitKmh; }

    public Integer getLanes() { return lanes; }
    public void setLanes(Integer lanes) { this.lanes = lanes; }

    public Boolean getIsToll() { return isToll; }
    public void setIsToll(Boolean isToll) { this.isToll = isToll; }

    public Boolean getIsBidirectional() { return isBidirectional; }
    public void setIsBidirectional(Boolean isBidirectional) { this.isBidirectional = isBidirectional; }
}
