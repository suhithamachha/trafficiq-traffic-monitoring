package com.trafficiq.repository;

import com.trafficiq.entity.TrafficIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<TrafficIncident, Long> {
    List<TrafficIncident> findByStatusInOrderByReportedAtDesc(List<TrafficIncident.Status> statuses);

    List<TrafficIncident> findByRoadRoadId(Long roadId);

    long countByStatusIn(List<TrafficIncident.Status> statuses);
}
