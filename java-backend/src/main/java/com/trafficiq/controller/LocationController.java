package com.trafficiq.controller;

import com.trafficiq.dto.ApiResponse;
import com.trafficiq.entity.Location;
import com.trafficiq.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired private LocationRepository locationRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Location>>> getAllLocations() {
        List<Location> list = locationRepository.findAllByOrderByLocationNameAsc();
        return ResponseEntity.ok(ApiResponse.ok("Spatial junction locations retrieved.", list));
    }
}
