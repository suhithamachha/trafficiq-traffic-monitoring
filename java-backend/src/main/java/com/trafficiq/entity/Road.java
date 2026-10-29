package com.trafficiq.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "roads")
public class Road {

    public enum RoadType {
        EXPRESSWAY, ARTERIAL, SUB_ARTERIAL, COLLECTOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "road_id")
    private Long roadId;

    @Column(name = "road_name", nullable = false, length = 150)
    private String roadName;

    @Column(name = "road_code", nullable = false, unique = true, length = 50)
    private String roadCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "end_location_id", nullable = false)
    private Location endLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "road_type", nullable = false, length = 30)
    private RoadType roadType = RoadType.ARTERIAL;

    @Column(name = "distance_km", nullable = false, precision = 6, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "speed_limit_kmh", nullable = false)
    private Integer speedLimitKmh = 50;

    @Column(nullable = false)
    private Integer lanes = 4;

    @Column(name = "is_toll", nullable = false)
    private Boolean isToll = false;

    @Column(name = "is_bidirectional", nullable = false)
    private Boolean isBidirectional = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Road() {}

    public Long getRoadId() { return roadId; }
    public void setRoadId(Long roadId) { this.roadId = roadId; }

    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }

    public String getRoadCode() { return roadCode; }
    public void setRoadCode(String roadCode) { this.roadCode = roadCode; }

    public Location getStartLocation() { return startLocation; }
    public void setStartLocation(Location startLocation) { this.startLocation = startLocation; }

    public Location getEndLocation() { return endLocation; }
    public void setEndLocation(Location endLocation) { this.endLocation = endLocation; }

    public RoadType getRoadType() { return roadType; }
    public void setRoadType(RoadType roadType) { this.roadType = roadType; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
