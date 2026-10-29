package com.trafficiq.dto;

import jakarta.validation.constraints.NotNull;

public class RoutePlanRequest {

    @NotNull(message = "Origin location ID is required")
    private Long originLocationId;

    @NotNull(message = "Destination location ID is required")
    private Long destinationLocationId;

    private String preference = "BALANCED"; // FASTEST, SHORTEST, BALANCED
    private String transportMode = "CAR"; // CAR, BIKE
    private Boolean avoidTolls = false;

    public RoutePlanRequest() {}

    public Long getOriginLocationId() { return originLocationId; }
    public void setOriginLocationId(Long originLocationId) { this.originLocationId = originLocationId; }

    public Long getDestinationLocationId() { return destinationLocationId; }
    public void setDestinationLocationId(Long destinationLocationId) { this.destinationLocationId = destinationLocationId; }

    public String getPreference() { return preference; }
    public void setPreference(String preference) { this.preference = preference; }

    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }

    public Boolean getAvoidTolls() { return avoidTolls; }
    public void setAvoidTolls(Boolean avoidTolls) { this.avoidTolls = avoidTolls; }
}
