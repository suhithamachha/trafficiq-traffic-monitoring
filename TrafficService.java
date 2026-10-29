package com.trafficiq.service;

import com.trafficiq.dto.RoadDto;
import com.trafficiq.dto.TrafficSummaryDto;
import com.trafficiq.entity.TrafficIncident;
import com.trafficiq.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TrafficService {

    @Autowired private RoadService roadService;
    @Autowired private IncidentRepository incidentRepository;

    public TrafficSummaryDto getTrafficSummary() {
        List<RoadDto> roads = roadService.getAllRoads();
        long activeIncidents = incidentRepository.countByStatusIn(
                Arrays.asList(TrafficIncident.Status.REPORTED, TrafficIncident.Status.IN_PROGRESS)
        );

        Map<String, Long> breakdown = new HashMap<>();
        breakdown.put("LOW", 0L);
        breakdown.put("MODERATE", 0L);
        breakdown.put("HEAVY", 0L);
        breakdown.put("SEVERE", 0L);

        BigDecimal speedSum = BigDecimal.ZERO;
        int speedCount = 0;

        for (RoadDto r : roads) {
            String lvl = r.getCongestionLevel() != null ? r.getCongestionLevel() : "LOW";
            breakdown.put(lvl, breakdown.getOrDefault(lvl, 0L) + 1);

            if (r.getCurrentSpeedKmh() != null) {
                speedSum = speedSum.add(r.getCurrentSpeedKmh());
                speedCount++;
            }
        }

        BigDecimal avgSpeed = speedCount > 0 ?
                speedSum.divide(new BigDecimal(speedCount), 1, RoundingMode.HALF_UP) :
                new BigDecimal("45.0");

        long severeCount = breakdown.getOrDefault("SEVERE", 0L);
        long heavyCount = breakdown.getOrDefault("HEAVY", 0L);
        String status = severeCount > 3 ? "HIGH CONGESTION" : (heavyCount > 4 ? "MODERATE CONGESTION" : "FLOWING NORMALLY");

        TrafficSummaryDto summary = new TrafficSummaryDto();
        summary.setTotalRoads(roads.size());
        summary.setActiveIncidents(activeIncidents);
        summary.setAverageNetworkSpeedKmh(avgSpeed);
        summary.setCongestionBreakdown(breakdown);
        summary.setSimulated(true);
        summary.setNetworkStatus(status);

        return summary;
    }
}
