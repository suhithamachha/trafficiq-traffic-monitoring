package com.trafficiq.controller;

import com.trafficiq.dto.ApiResponse;
import com.trafficiq.dto.CreateRoadRequest;
import com.trafficiq.dto.RoadDto;
import com.trafficiq.service.RoadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roads")
public class RoadController {

    @Autowired private RoadService roadService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoadDto>>> getAllRoads() {
        List<RoadDto> roads = roadService.getAllRoads();
        return ResponseEntity.ok(ApiResponse.ok("Road network segments retrieved successfully.", roads));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoadDto>> getRoadById(@PathVariable Long id) {
        RoadDto road = roadService.getRoadById(id);
        return ResponseEntity.ok(ApiResponse.ok("Road segment retrieved.", road));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoadDto>> createRoad(@Valid @RequestBody CreateRoadRequest req) {
        RoadDto created = roadService.createRoad(req);
        return ResponseEntity.status(201).body(ApiResponse.ok("Road segment created successfully.", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoadDto>> updateRoad(@PathVariable Long id, @Valid @RequestBody CreateRoadRequest req) {
        RoadDto updated = roadService.updateRoad(id, req);
        return ResponseEntity.ok(ApiResponse.ok("Road segment updated successfully.", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoad(@PathVariable Long id) {
        roadService.deleteRoad(id);
        return ResponseEntity.ok(ApiResponse.ok("Road segment deleted successfully.", null));
    }
}
