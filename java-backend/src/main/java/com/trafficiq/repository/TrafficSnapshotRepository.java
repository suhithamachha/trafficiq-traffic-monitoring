package com.trafficiq.repository;

import com.trafficiq.entity.Road;
import com.trafficiq.entity.TrafficSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficSnapshotRepository extends JpaRepository<TrafficSnapshot, Long> {
    Optional<TrafficSnapshot> findTopByRoadOrderByRecordedAtDesc(Road road);

    Optional<TrafficSnapshot> findTopByRoadRoadIdOrderByRecordedAtDesc(Long roadId);

    @Query("SELECT s FROM TrafficSnapshot s WHERE s.snapshotId IN " +
           "(SELECT MAX(sub.snapshotId) FROM TrafficSnapshot sub GROUP BY sub.road.roadId)")
    List<TrafficSnapshot> findLatestSnapshotsForAllRoads();

    @Query("SELECT s FROM TrafficSnapshot s WHERE s.road.roadId = :roadId ORDER BY s.recordedAt DESC")
    List<TrafficSnapshot> findRecentSnapshotsForRoad(@Param("roadId") Long roadId);
}
