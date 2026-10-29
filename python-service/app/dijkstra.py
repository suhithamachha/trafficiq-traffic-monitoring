"""
Dijkstra and Heuristic Routing Engine with Dynamic Congestion & Incident Penalties.
"""
import heapq
import math
from typing import List, Tuple, Dict, Optional, Set
from app.models import (
    RoadEdge, LocationNode, CongestionLevel, RoutePreference, 
    RouteOption, RouteSegmentResponse
)
from app.graph import RoadNetworkGraph


# Congestion speed factors
CONGESTION_SPEED_FACTORS = {
    CongestionLevel.LOW: 1.00,
    CongestionLevel.MODERATE: 0.70,
    CongestionLevel.HEAVY: 0.40,
    CongestionLevel.SEVERE: 0.20,
}


def haversine_distance_km(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """Computes great-circle distance between two GPS coordinates in kilometers."""
    R = 6371.0  # Earth radius in kilometers
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = (
        math.sin(dlat / 2) ** 2
        + math.cos(math.radians(lat1))
        * math.cos(math.radians(lat2))
        * math.sin(dlon / 2) ** 2
    )
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c


def compute_edge_cost(
    road: RoadEdge, 
    preference: RoutePreference, 
    penalized_roads: Optional[Set[int]] = None
) -> Tuple[float, float]:
    """
    Computes (cost_for_algorithm, duration_minutes).
    Weight dynamically factors physical distance, traffic congestion, and active incident delays.
    """
    # 1. Effective speed calculation
    factor = CONGESTION_SPEED_FACTORS.get(road.congestion_level, 0.70)
    effective_speed = max(10.0, road.speed_limit_kmh * factor)

    # 2. Travel duration in minutes
    base_duration_min = (road.distance_km / effective_speed) * 60.0
    incident_delay = road.impact_delay_minutes if road.active_incidents > 0 else 0.0
    total_duration_min = base_duration_min + incident_delay

    # 3. Preference-specific cost
    if preference == RoutePreference.SHORTEST:
        cost = road.distance_km
    elif preference == RoutePreference.FASTEST:
        cost = total_duration_min
    else:  # BALANCED
        # 50% time, 30% distance, 20% incident risk factor
        congestion_penalty = (
            2.0 if road.congestion_level == CongestionLevel.SEVERE else
            1.4 if road.congestion_level == CongestionLevel.HEAVY else
            1.1 if road.congestion_level == CongestionLevel.MODERATE else 1.0
        )
        cost = (total_duration_min * 0.6) + (road.distance_km * 0.4 * congestion_penalty)

    # Apply penalty multiplier for alternative route discovery
    if penalized_roads and road.road_id in penalized_roads:
        cost *= 2.2

    return cost, total_duration_min


def calculate_route_score(
    total_dist_km: float, 
    total_time_min: float, 
    has_severe: bool, 
    active_incidents_count: int
) -> float:
    """
    Calculates efficiency score from 1.0 to 99.0.
    100 is theoretical perfect free-flow.
    """
    base_score = 100.0
    # Penalty for excessive duration relative to distance
    if total_dist_km > 0:
        avg_speed = (total_dist_km / (total_time_min / 60.0))
        if avg_speed < 20.0:
            base_score -= 20.0
        elif avg_speed < 35.0:
            base_score -= 10.0
    
    if has_severe:
        base_score -= 15.0
    base_score -= (active_incidents_count * 5.0)

    return max(15.0, min(98.5, round(base_score, 1)))


class DijkstraRouter:
    """Dijkstra and A* routing solver on urban road network."""

    def __init__(self, graph: RoadNetworkGraph):
        self.graph = graph

    def find_route(
        self,
        origin_id: int,
        destination_id: int,
        preference: RoutePreference = RoutePreference.BALANCED,
        penalized_roads: Optional[Set[int]] = None,
        route_label: str = "Primary Route",
        algorithm: str = "DIJKSTRA"
    ) -> Optional[RouteOption]:
        if origin_id not in self.graph.locations or destination_id not in self.graph.locations:
            return None

        # Min-priority queue: (current_cost, current_node, path_history, road_sequence)
        pq: List[Tuple[float, int, List[int], List[RoadEdge]]] = [(0.0, origin_id, [origin_id], [])]
        min_costs: Dict[int, float] = {origin_id: 0.0}

        dest_node = self.graph.get_location(destination_id)

        while pq:
            current_cost, u, path, roads_traversed = heapq.heappop(pq)

            if u == destination_id:
                # Target reached - construct RouteOption
                total_distance = sum(r.distance_km for r in roads_traversed)
                total_duration = 0.0
                segments: List[RouteSegmentResponse] = []
                has_severe = False
                active_incidents = 0

                for i, r in enumerate(roads_traversed, start=1):
                    _, seg_duration = compute_edge_cost(r, preference)
                    total_duration += seg_duration
                    if r.congestion_level == CongestionLevel.SEVERE:
                        has_severe = True
                    active_incidents += r.active_incidents

                    u_loc = self.graph.get_location(path[i - 1])
                    v_loc = self.graph.get_location(path[i])
                    coords = [
                        [u_loc.latitude, u_loc.longitude],
                        [v_loc.latitude, v_loc.longitude]
                    ]

                    segments.append(
                        RouteSegmentResponse(
                            sequence=i,
                            road_id=r.road_id,
                            road_name=r.road_name,
                            start_location_id=u_loc.location_id,
                            start_location_name=u_loc.location_name,
                            end_location_id=v_loc.location_id,
                            end_location_name=v_loc.location_name,
                            distance_km=round(r.distance_km, 2),
                            duration_minutes=round(seg_duration, 1),
                            congestion_level=r.congestion_level,
                            coordinates=coords
                        )
                    )

                score = calculate_route_score(total_distance, total_duration, has_severe, active_incidents)
                advisory = f"{preference.value} route computed via {len(segments)} segments. Est. speed: {round((total_distance / max(0.1, total_duration / 60)), 1)} km/h."
                if active_incidents > 0:
                    advisory += f" Warning: {active_incidents} active incident(s) reported along this corridor."

                return RouteOption(
                    route_name=route_label,
                    preference=preference,
                    total_distance_km=round(total_distance, 2),
                    estimated_duration_minutes=round(total_duration, 1),
                    route_score=score,
                    algorithm_used=algorithm,
                    segments=segments,
                    path_node_ids=path,
                    summary_advisory=advisory
                )

            if current_cost > min_costs.get(u, float("inf")):
                continue

            for v, road in self.graph.get_neighbors(u):
                edge_cost, _ = compute_edge_cost(road, preference, penalized_roads)
                heuristic = 0.0
                if algorithm == "A_STAR":
                    v_node = self.graph.get_location(v)
                    heuristic = haversine_distance_km(v_node.latitude, v_node.longitude, dest_node.latitude, dest_node.longitude)

                new_cost = current_cost + edge_cost + (heuristic * 0.2 if preference == RoutePreference.SHORTEST else 0.0)

                if new_cost < min_costs.get(v, float("inf")):
                    min_costs[v] = new_cost
                    heapq.heappush(pq, (new_cost, v, path + [v], roads_traversed + [road]))

        return None

    def find_alternative_routes(
        self, origin_id: int, destination_id: int, preference: RoutePreference
    ) -> Tuple[Optional[RouteOption], List[RouteOption]]:
        """Finds primary optimal route and up to 2 distinct alternative routes."""
        primary = self.find_route(
            origin_id, destination_id, preference, 
            route_label="Recommended (Optimal)", algorithm="DIJKSTRA"
        )
        if not primary or not primary.segments:
            return None, []

        alternatives: List[RouteOption] = []
        penalized_set: Set[int] = set()

        # Alternative 1: Penalize primary edges
        for seg in primary.segments:
            penalized_set.add(seg.road_id)

        alt1 = self.find_route(
            origin_id, destination_id, preference, 
            penalized_roads=penalized_set,
            route_label="Alternative 1 (Bypass / Arterial)", algorithm="A_STAR"
        )
        if alt1 and alt1.path_node_ids != primary.path_node_ids:
            alternatives.append(alt1)

        # Alternative 2: Switch preference (e.g. if FASTEST, try SHORTEST)
        alt_pref = RoutePreference.SHORTEST if preference == RoutePreference.FASTEST else RoutePreference.FASTEST
        alt2 = self.find_route(
            origin_id, destination_id, alt_pref,
            penalized_roads=penalized_set,
            route_label=f"Alternative 2 ({alt_pref.value})", algorithm="DIJKSTRA"
        )
        if alt2 and alt2.path_node_ids != primary.path_node_ids and (not alternatives or alt2.path_node_ids != alternatives[0].path_node_ids):
            alternatives.append(alt2)

        return primary, alternatives
