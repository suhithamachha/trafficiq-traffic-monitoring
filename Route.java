package com.trafficiq.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
public class Route {

    public enum RoutePreference {
        FASTEST, SHORTEST, BALANCED
    }

    public enum AlgorithmUsed {
        DIJKSTRA, A_STAR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "route_name", nullable = false, length = 150)
    private String routeName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origin_location_id", nullable = false)
    private Location originLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destinationLocation;

    @Column(name = "total_distance_km", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalDistanceKm;

    @Column(name = "estimated_duration_minutes", nullable = false, precision = 6, scale = 2)
    private BigDecimal estimatedDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_preference", nullable = false, length = 20)
    private RoutePreference routePreference = RoutePreference.BALANCED;

    @Column(name = "route_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal routeScore = new BigDecimal("85.00");

    @Enumerated(EnumType.STRING)
    @Column(name = "algorithm_used", nullable = false, length = 20)
    private AlgorithmUsed algorithmUsed = AlgorithmUsed.DIJKSTRA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Route() {}

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public Location getOriginLocation() { return originLocation; }
    public void setOriginLocation(Location originLocation) { this.originLocation = originLocation; }

    public Location getDestinationLocation() { return destinationLocation; }
    public void setDestinationLocation(Location destinationLocation) { this.destinationLocation = destinationLocation; }

    public BigDecimal getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(BigDecimal totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public BigDecimal getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(BigDecimal estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public RoutePreference getRoutePreference() { return routePreference; }
    public void setRoutePreference(RoutePreference routePreference) { this.routePreference = routePreference; }

    public BigDecimal getRouteScore() { return routeScore; }
    public void setRouteScore(BigDecimal routeScore) { this.routeScore = routeScore; }

    public AlgorithmUsed getAlgorithmUsed() { return algorithmUsed; }
    public void setAlgorithmUsed(AlgorithmUsed algorithmUsed) { this.algorithmUsed = algorithmUsed; }

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
