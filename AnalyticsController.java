package com.trafficiq.controller;

import com.trafficiq.dto.ApiResponse;
import com.trafficiq.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired private AnalyticsService analyticsService;

    @GetMapping("/data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalyticsData() {
        Map<String, Object> data = analyticsService.getAnalyticsData();
        return ResponseEntity.ok(ApiResponse.ok("Traffic analytics telemetry retrieved.", data));
    }

    @GetMapping("/export-csv")
    public ResponseEntity<String> exportCsv() {
        String csv = analyticsService.generateCsvReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"trafficiq_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
