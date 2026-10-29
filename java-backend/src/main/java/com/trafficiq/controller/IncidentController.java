package com.trafficiq.controller;

import com.trafficiq.dto.ApiResponse;
import com.trafficiq.dto.CreateIncidentRequest;
import com.trafficiq.dto.IncidentDto;
import com.trafficiq.dto.UpdateIncidentRequest;
import com.trafficiq.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    @Autowired private IncidentService incidentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IncidentDto>>> getAllIncidents() {
        List<IncidentDto> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(ApiResponse.ok("Incidents retrieved successfully.", incidents));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<IncidentDto>>> getActiveIncidents() {
        List<IncidentDto> active = incidentService.getActiveIncidents();
        return ResponseEntity.ok(ApiResponse.ok("Active incidents retrieved successfully.", active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentDto>> getIncidentById(@PathVariable Long id) {
        IncidentDto inc = incidentService.getIncidentById(id);
        return ResponseEntity.ok(ApiResponse.ok("Incident retrieved.", inc));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IncidentDto>> createIncident(@Valid @RequestBody CreateIncidentRequest req) {
        IncidentDto created = incidentService.createIncident(req);
        return ResponseEntity.status(201).body(ApiResponse.ok("Traffic incident reported successfully.", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentDto>> updateIncident(
            @PathVariable Long id, @RequestBody UpdateIncidentRequest req) {
        IncidentDto updated = incidentService.updateIncident(id, req);
        return ResponseEntity.ok(ApiResponse.ok("Incident status updated successfully.", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.ok(ApiResponse.ok("Incident deleted successfully.", null));
    }
}
