package com.trafficiq.dto;

import java.math.BigDecimal;
import java.util.Map;

public class TrafficSummaryDto {
    private long totalRoads;
    private long activeIncidents;
    private BigDecimal averageNetworkSpeedKmh;
    private Map<String, Long> congestionBreakdown;
    private boolean isSimulated = true;
    private String networkStatus;

    public TrafficSummaryDto() {}

    public long getTotalRoads() { return totalRoads; }
    public void setTotalRoads(long totalRoads) { this.totalRoads = totalRoads; }

    public long getActiveIncidents() { return activeIncidents; }
    public void setActiveIncidents(long activeIncidents) { this.activeIncidents = activeIncidents; }

    public BigDecimal getAverageNetworkSpeedKmh() { return averageNetworkSpeedKmh; }
    public void setAverageNetworkSpeedKmh(BigDecimal averageNetworkSpeedKmh) { this.averageNetworkSpeedKmh = averageNetworkSpeedKmh; }

    public Map<String, Long> getCongestionBreakdown() { return congestionBreakdown; }
    public void setCongestionBreakdown(Map<String, Long> congestionBreakdown) { this.congestionBreakdown = congestionBreakdown; }

    public boolean isSimulated() { return isSimulated; }
    public void setSimulated(boolean simulated) { isSimulated = simulated; }

    public String getNetworkStatus() { return networkStatus; }
    public void setNetworkStatus(String networkStatus) { this.networkStatus = networkStatus; }
}
