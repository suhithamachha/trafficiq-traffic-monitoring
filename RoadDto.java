package com.trafficiq.dto;

import java.math.BigDecimal;

public class RoadDto {
    private Long roadId;
    private String roadName;
    private String roadCode;
    private Long startLocationId;
    private String startLocationName;
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private Long endLocationId;
    private String endLocationName;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;
    private String roadType;
    private BigDecimal distanceKm;
    private Integer speedLimitKmh;
    private Integer lanes;
    private Boolean isToll;
    private Boolean isBidirectional;
    private String congestionLevel;
    private BigDecimal currentSpeedKmh;
    private BigDecimal estimatedTravelTimeMin;
    private Integer activeIncidents;

    public RoadDto() {}

    public Long getRoadId() { return roadId; }
    public void setRoadId(Long roadId) { this.roadId = roadId; }

    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }

    public String getRoadCode() { return roadCode; }
    public void setRoadCode(String roadCode) { this.roadCode = roadCode; }

    public Long getStartLocationId() { return startLocationId; }
    public void setStartLocationId(Long startLocationId) { this.startLocationId = startLocationId; }

    public String getStartLocationName() { return startLocationName; }
    public void setStartLocationName(String startLocationName) { this.startLocationName = startLocationName; }

    public BigDecimal getStartLatitude() { return startLatitude; }
    public void setStartLatitude(BigDecimal startLatitude) { this.startLatitude = startLatitude; }

    public BigDecimal getStartLongitude() { return startLongitude; }
    public void setStartLongitude(BigDecimal startLongitude) { this.startLongitude = startLongitude; }

    public Long getEndLocationId() { return endLocationId; }
    public void setEndLocationId(Long endLocationId) { this.endLocationId = endLocationId; }

    public String getEndLocationName() { return endLocationName; }
    public void setEndLocationName(String endLocationName) { this.endLocationName = endLocationName; }

    public BigDecimal getEndLatitude() { return endLatitude; }
    public void setEndLatitude(BigDecimal endLatitude) { this.endLatitude = endLatitude; }

    public BigDecimal getEndLongitude() { return endLongitude; }
    public void setEndLongitude(BigDecimal endLongitude) { this.endLongitude = endLongitude; }

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

    public String getCongestionLevel() { return congestionLevel; }
    public void setCongestionLevel(String congestionLevel) { this.congestionLevel = congestionLevel; }

    public BigDecimal getCurrentSpeedKmh() { return currentSpeedKmh; }
    public void setCurrentSpeedKmh(BigDecimal currentSpeedKmh) { this.currentSpeedKmh = currentSpeedKmh; }

    public BigDecimal getEstimatedTravelTimeMin() { return estimatedTravelTimeMin; }
    public void setEstimatedTravelTimeMin(BigDecimal estimatedTravelTimeMin) { this.estimatedTravelTimeMin = estimatedTravelTimeMin; }

    public Integer getActiveIncidents() { return activeIncidents; }
    public void setActiveIncidents(Integer activeIncidents) { this.activeIncidents = activeIncidents; }
}
