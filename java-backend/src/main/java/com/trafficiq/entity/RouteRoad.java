package com.trafficiq.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "route_roads")
public class RouteRoad {

    @EmbeddedId
    private RouteRoadId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("routeId")
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roadId")
    @JoinColumn(name = "road_id")
    private Road road;

    @Column(name = "segment_distance_km", nullable = false, precision = 5, scale = 2)
    private BigDecimal segmentDistanceKm;

    public RouteRoad() {}

    public RouteRoad(Route route, Road road, Integer sequenceNumber, BigDecimal segmentDistanceKm) {
        this.id = new RouteRoadId(route.getRouteId(), road.getRoadId(), sequenceNumber);
        this.route = route;
        this.road = road;
        this.segmentDistanceKm = segmentDistanceKm;
    }

    public RouteRoadId getId() { return id; }
    public void setId(RouteRoadId id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Road getRoad() { return road; }
    public void setRoad(Road road) { this.road = road; }

    public BigDecimal getSegmentDistanceKm() { return segmentDistanceKm; }
    public void setSegmentDistanceKm(BigDecimal segmentDistanceKm) { this.segmentDistanceKm = segmentDistanceKm; }
}
