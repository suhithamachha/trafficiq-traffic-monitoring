package com.trafficiq.repository;

import com.trafficiq.entity.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoadRepository extends JpaRepository<Road, Long> {
    Optional<Road> findByRoadCode(String roadCode);

    List<Road> findByStartLocationLocationId(Long startLocationId);

    @Query("SELECT r FROM Road r WHERE (r.startLocation.locationId = :u AND r.endLocation.locationId = :v) " +
           "OR (r.isBidirectional = true AND r.startLocation.locationId = :v AND r.endLocation.locationId = :u)")
    List<Road> findConnectingRoads(@Param("u") Long u, @Param("v") Long v);
}
