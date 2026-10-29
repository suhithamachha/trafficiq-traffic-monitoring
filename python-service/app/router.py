"""
FastAPI route handlers for traffic analysis and intelligent route planning.
"""
from fastapi import APIRouter, HTTPException, status
from typing import List
from app.models import (
    RouteOptimizeRequest, RouteComparisonResponse, 
    TrafficAnalysisRequest, TrafficAnalysisResponse,
    LocationNode, RoadEdge, CongestionLevel
)
from app.graph import RoadNetworkGraph, HYDERABAD_LOCATIONS, HYDERABAD_DEFAULT_ROADS
from app.dijkstra import DijkstraRouter

api_router = APIRouter()


@api_router.get("/health", tags=["System"])
def health_check():
    """Health status check for Python Traffic Intelligence Service."""
    return {
        "status": "UP",
        "service": "TrafficiQ Python Traffic Intelligence Engine",
        "version": "1.0.0",
        "graph_nodes": len(HYDERABAD_LOCATIONS),
        "graph_edges": len(HYDERABAD_DEFAULT_ROADS),
        "environment": "academic_demonstration"
    }


@api_router.get("/locations", response_model=List[LocationNode], tags=["Network"])
def get_locations():
    """Returns all mapped metropolitan junction nodes in Hyderabad."""
    return list(HYDERABAD_LOCATIONS.values())


@api_router.get("/roads", response_model=List[RoadEdge], tags=["Network"])
def get_roads():
    """Returns baseline road segments."""
    return HYDERABAD_DEFAULT_ROADS


@api_router.post("/optimize-route", response_model=RouteComparisonResponse, tags=["Routing"])
def optimize_route(request: RouteOptimizeRequest):
    """
    Computes optimal route using Dijkstra's algorithm and discovers up to 2 alternative routes
    considering distance, speed limits, congestion factors, and active incident delays.
    """
    if request.origin_location_id == request.destination_location_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Origin and destination locations must be distinct."
        )

    # Build graph with custom roads if provided from live DB, otherwise baseline
    graph = RoadNetworkGraph(roads=request.custom_roads or HYDERABAD_DEFAULT_ROADS)

    origin_loc = graph.get_location(request.origin_location_id)
    dest_loc = graph.get_location(request.destination_location_id)

    if not origin_loc:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Origin location ID {request.origin_location_id} not found in road network."
        )
    if not dest_loc:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Destination location ID {request.destination_location_id} not found in road network."
        )

    router = DijkstraRouter(graph)
    primary, alternatives = router.find_alternative_routes(
        request.origin_location_id, request.destination_location_id, request.preference
    )

    if not primary:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No viable route found connecting the selected locations."
        )

    advisory = (
        f"Route plan calculated based on simulated traffic telemetry. "
        f"Primary path spans {primary.total_distance_km} km with an estimated travel time of {primary.estimated_duration_minutes} minutes."
    )

    return RouteComparisonResponse(
        origin_location=origin_loc,
        destination_location=dest_loc,
        primary_route=primary,
        alternative_routes=alternatives,
        is_simulated_data=True,
        advisory_notes=advisory
    )


@api_router.post("/compare-routes", response_model=RouteComparisonResponse, tags=["Routing"])
def compare_routes(request: RouteOptimizeRequest):
    """Alias for route optimization and multi-path comparison."""
    return optimize_route(request)


@api_router.post("/analyze-traffic", response_model=TrafficAnalysisResponse, tags=["Analytics"])
def analyze_traffic(request: TrafficAnalysisRequest):
    """
    Analyzes holistic network traffic health, identifying severe bottlenecks,
    average speeds, and congestion distributions across road segments.
    """
    roads = request.roads if request.roads is not None else HYDERABAD_DEFAULT_ROADS

    total = len(roads)
    breakdown = {
        CongestionLevel.LOW.value: 0,
        CongestionLevel.MODERATE.value: 0,
        CongestionLevel.HEAVY.value: 0,
        CongestionLevel.SEVERE.value: 0,
    }
    total_speed = 0.0
    bottlenecks = []

    for r in roads:
        lvl = r.congestion_level.value if hasattr(r.congestion_level, "value") else str(r.congestion_level)
        breakdown[lvl] = breakdown.get(lvl, 0) + 1
        total_speed += r.speed_limit_kmh * (
            1.0 if lvl == "LOW" else 0.7 if lvl == "MODERATE" else 0.4 if lvl == "HEAVY" else 0.2
        )
        if lvl in ["HEAVY", "SEVERE"] or r.active_incidents > 0:
            bottlenecks.append({
                "road_id": str(r.road_id),
                "road_name": r.road_name,
                "congestion": lvl,
                "reason": f"{lvl} congestion" + (f" with {r.active_incidents} active incident(s)" if r.active_incidents else "")
            })

    avg_speed = round(total_speed / max(1, total), 1)

    # Health score: 100 max, reduced by severe & heavy bottlenecks
    severe_count = breakdown.get("SEVERE", 0)
    heavy_count = breakdown.get("HEAVY", 0)
    health_score = max(10.0, round(100.0 - (severe_count * 12.0) - (heavy_count * 5.0), 1))

    status_label = (
        "EXCELLENT" if health_score >= 85 else
        "MODERATE CONGESTION" if health_score >= 65 else
        "HEAVY DELAYS" if health_score >= 45 else "GRIDLOCK ALERT"
    )

    return TrafficAnalysisResponse(
        total_roads_monitored=total,
        congestion_breakdown=breakdown,
        bottlenecks=bottlenecks,
        average_speed_kmh=avg_speed,
        network_health_score=health_score,
        status_label=status_label,
        is_simulated=True
    )
