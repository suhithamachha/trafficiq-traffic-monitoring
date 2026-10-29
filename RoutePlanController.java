package com.trafficiq.controller;

import com.trafficiq.dto.ApiResponse;
import com.trafficiq.dto.RoutePlanRequest;
import com.trafficiq.service.RoutePlanningService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RoutePlanController {

    @Autowired private RoutePlanningService routePlanningService;

    @PostMapping("/plan")
    public ResponseEntity<ApiResponse<Map<String, Object>>> planRoute(@Valid @RequestBody RoutePlanRequest req) {
        Map<String, Object> result = routePlanningService.planRoute(req);
        return ResponseEntity.ok(ApiResponse.ok("Intelligent route calculation completed.", result));
    }
}
