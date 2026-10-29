package com.trafficiq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RouteRoadId implements Serializable {

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "road_id")
    private Long roadId;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    public RouteRoadId() {}

    public RouteRoadId(Long routeId, Long roadId, Integer sequenceNumber) {
        this.routeId = routeId;
        this.roadId = roadId;
        this.sequenceNumber = sequenceNumber;
    }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getRoadId() { return roadId; }
    public void setRoadId(Long roadId) { this.roadId = roadId; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteRoadId)) return false;
        RouteRoadId that = (RouteRoadId) o;
        return Objects.equals(routeId, that.routeId) &&
               Objects.equals(roadId, that.roadId) &&
               Objects.equals(sequenceNumber, that.sequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, roadId, sequenceNumber);
    }
}
