"""
Pydantic Data Models for Traffic Intelligence and Route Optimization Service.
"""
from enum import Enum
from typing import List, Optional, Dict
from pydantic import BaseModel, Field


class CongestionLevel(str, Enum):
    LOW = "LOW"
    MODERATE = "MODERATE"
    HEAVY = "HEAVY"
    SEVERE = "SEVERE"


class RoutePreference(str, Enum):
    FASTEST = "FASTEST"
    SHORTEST = "SHORTEST"
    BALANCED = "BALANCED"


class TransportMode(str, Enum):
    CAR = "CAR"
    BIKE = "BIKE"


class LocationNode(BaseModel):
    location_id: int
    location_name: str
    latitude: float
    longitude: float
    area_code: Optional[str] = None
    landmark: Optional[str] = None


class RoadEdge(BaseModel):
    road_id: int
    road_name: str
    road_code: str
    start_location_id: int
    end_location_id: int
    distance_km: float
    speed_limit_kmh: int
    congestion_level: CongestionLevel = CongestionLevel.LOW
    road_type: str = "ARTERIAL"
    active_incidents: int = 0
    impact_delay_minutes: int = 0
    is_bidirectional: bool = True


class RouteOptimizeRequest(BaseModel):
    origin_location_id: int = Field(..., description="ID of origin location")
    destination_location_id: int = Field(..., description="ID of destination location")
    preference: RoutePreference = RoutePreference.BALANCED
    transport_mode: TransportMode = TransportMode.CAR
    avoid_tolls: bool = False
    custom_roads: Optional[List[RoadEdge]] = Field(
        None, description="Optional custom road states passed from live DB"
    )


class RouteSegmentResponse(BaseModel):
    sequence: int
    road_id: int
    road_name: str
    start_location_id: int
    start_location_name: str
    end_location_id: int
    end_location_name: str
    distance_km: float
    duration_minutes: float
    congestion_level: CongestionLevel
    coordinates: List[List[float]] = Field(
        default_factory=list, description="[[lat, lng], [lat, lng]]"
    )


class RouteOption(BaseModel):
    route_name: str
    preference: RoutePreference
    total_distance_km: float
    estimated_duration_minutes: float
    route_score: float = Field(..., description="Route efficiency index 0-100")
    algorithm_used: str = "DIJKSTRA"
    segments: List[RouteSegmentResponse]
    path_node_ids: List[int]
    summary_advisory: str


class RouteComparisonResponse(BaseModel):
    origin_location: LocationNode
    destination_location: LocationNode
    primary_route: RouteOption
    alternative_routes: List[RouteOption]
    is_simulated_data: bool = True
    advisory_notes: str


class TrafficAnalysisRequest(BaseModel):
    roads: Optional[List[RoadEdge]] = None


class TrafficAnalysisResponse(BaseModel):
    total_roads_monitored: int
    congestion_breakdown: Dict[str, int]
    bottlenecks: List[Dict[str, str]]
    average_speed_kmh: float
    network_health_score: float
    status_label: str
    is_simulated: bool = True
