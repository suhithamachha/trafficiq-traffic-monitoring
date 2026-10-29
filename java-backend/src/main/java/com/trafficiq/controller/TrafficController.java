package com.trafficiq.controller;

import com.trafficiq.dto.ApiResponse;
import com.trafficiq.dto.TrafficSummaryDto;
import com.trafficiq.service.TrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {

    @Autowired private TrafficService trafficService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<TrafficSummaryDto>> getSummary() {
        TrafficSummaryDto summary = trafficService.getTrafficSummary();
        return ResponseEntity.ok(ApiResponse.ok("Traffic summary telemetry retrieved.", summary));
    }
}
