"""
Unit tests for Python Traffic Intelligence Service.
"""
import pytest
from fastapi.testclient import TestClient
from main import app
from app.graph import HYDERABAD_LOCATIONS, HYDERABAD_DEFAULT_ROADS
from app.dijkstra import DijkstraRouter, RoadNetworkGraph
from app.models import RoutePreference

client = TestClient(app)


def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "UP"
    assert data["graph_nodes"] == len(HYDERABAD_LOCATIONS)


def test_get_locations():
    response = client.get("/locations")
    assert response.status_code == 200
    locations = response.json()
    assert len(locations) >= 16
    hitec = next(l for l in locations if l["location_id"] == 1)
    assert "HITEC City" in hitec["location_name"]


def test_dijkstra_routing_algorithm():
    graph = RoadNetworkGraph(roads=HYDERABAD_DEFAULT_ROADS, locations=HYDERABAD_LOCATIONS)
    router = DijkstraRouter(graph)

    # Route from HITEC City (1) to Secunderabad (10)
    route = router.find_route(1, 10, preference=RoutePreference.FASTEST)
    assert route is not None
    assert route.total_distance_km > 0
    assert route.estimated_duration_minutes > 0
    assert len(route.segments) > 0
    assert route.path_node_ids[0] == 1
    assert route.path_node_ids[-1] == 10


def test_optimize_route_api():
    payload = {
        "origin_location_id": 1,
        "destination_location_id": 13,  # Shamshabad Airport
        "preference": "FASTEST",
        "transport_mode": "CAR",
        "avoid_tolls": False
    }
    response = client.post("/optimize-route", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["primary_route"] is not None
    assert data["primary_route"]["total_distance_km"] > 0
    assert data["is_simulated_data"] is True


def test_identical_origin_destination_rejected():
    payload = {
        "origin_location_id": 1,
        "destination_location_id": 1,
        "preference": "FASTEST"
    }
    response = client.post("/optimize-route", json=payload)
    assert response.status_code == 400


def test_traffic_analysis_api():
    response = client.post("/analyze-traffic", json={})
    assert response.status_code == 200
    data = response.json()
    assert data["total_roads_monitored"] > 0
    assert "LOW" in data["congestion_breakdown"]
    assert "network_health_score" in data
