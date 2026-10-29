package com.trafficiq.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_snapshots")
public class TrafficSnapshot {

    public enum CongestionLevel {
        LOW, MODERATE, HEAVY, SEVERE
    }

    public enum DataSource {
        DEMO_SIMULATED, SENSOR_VIRTUAL, POLICE_FEED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Long snapshotId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "road_id", nullable = false)
    private Road road;

    @Enumerated(EnumType.STRING)
    @Column(name = "congestion_level", nullable = false, length = 20)
    private CongestionLevel congestionLevel = CongestionLevel.LOW;

    @Column(name = "average_speed_kmh", nullable = false, precision = 5, scale = 2)
    private BigDecimal averageSpeedKmh;

    @Column(name = "vehicle_count", nullable = false)
    private Integer vehicleCount = 0;

    @Column(name = "estimated_travel_time_min", nullable = false, precision = 6, scale = 2)
    private BigDecimal estimatedTravelTimeMin;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "data_source", nullable = false, length = 30)
    private DataSource dataSource = DataSource.DEMO_SIMULATED;

    public TrafficSnapshot() {}

    public Long getSnapshotId() { return snapshotId; }
    public void setSnapshotId(Long snapshotId) { this.snapshotId = snapshotId; }

    public Road getRoad() { return road; }
    public void setRoad(Road road) { this.road = road; }

    public CongestionLevel getCongestionLevel() { return congestionLevel; }
    public void setCongestionLevel(CongestionLevel congestionLevel) { this.congestionLevel = congestionLevel; }

    public BigDecimal getAverageSpeedKmh() { return averageSpeedKmh; }
    public void setAverageSpeedKmh(BigDecimal averageSpeedKmh) { this.averageSpeedKmh = averageSpeedKmh; }

    public Integer getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(Integer vehicleCount) { this.vehicleCount = vehicleCount; }

    public BigDecimal getEstimatedTravelTimeMin() { return estimatedTravelTimeMin; }
    public void setEstimatedTravelTimeMin(BigDecimal estimatedTravelTimeMin) { this.estimatedTravelTimeMin = estimatedTravelTimeMin; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public DataSource getDataSource() { return dataSource; }
    public void setDataSource(DataSource dataSource) { this.dataSource = dataSource; }
}
