"""
Graph data structures and default Hyderabad metropolitan network definition.
"""
from typing import Dict, List, Tuple
from app.models import LocationNode, RoadEdge, CongestionLevel


# Realistic spatial coordinates for Hyderabad junctions
HYDERABAD_LOCATIONS: Dict[int, LocationNode] = {
    1: LocationNode(location_id=1, location_name="HITEC City (Cyber Towers)", latitude=17.4504, longitude=78.3808, area_code="500081", landmark="Cyber Towers"),
    2: LocationNode(location_id=2, location_name="Madhapur (Mindspace)", latitude=17.4399, longitude=78.3815, area_code="500081", landmark="Mindspace IT Park"),
    3: LocationNode(location_id=3, location_name="Gachibowli (Bio-Diversity)", latitude=17.4401, longitude=78.3489, area_code="500032", landmark="Bio-Diversity Flyover"),
    4: LocationNode(location_id=4, location_name="Financial District (Nanakramguda)", latitude=17.4156, longitude=78.3427, area_code="500032", landmark="WaveRock SEZ"),
    5: LocationNode(location_id=5, location_name="Jubilee Hills (Check Post / Rd 36)", latitude=17.4319, longitude=78.4073, area_code="500033", landmark="Check Post Metro"),
    6: LocationNode(location_id=6, location_name="Banjara Hills (Road No. 1)", latitude=17.4156, longitude=78.4354, area_code="500034", landmark="City Centre Mall"),
    7: LocationNode(location_id=7, location_name="Panjagutta Junction", latitude=17.4265, longitude=78.4526, area_code="500082", landmark="Panjagutta Flyover"),
    8: LocationNode(location_id=8, location_name="Ameerpet (Metro Interchange)", latitude=17.4375, longitude=78.4482, area_code="500016", landmark="Ameerpet Junction"),
    9: LocationNode(location_id=9, location_name="Kukatpally (KPHB Colony)", latitude=17.4938, longitude=78.3995, area_code="500072", landmark="JNTU / Forum Mall"),
    10: LocationNode(location_id=10, location_name="Secunderabad (Railway Station)", latitude=17.4399, longitude=78.4983, area_code="500003", landmark="Main Station"),
    11: LocationNode(location_id=11, location_name="Begumpet (Airport Road)", latitude=17.4448, longitude=78.4688, area_code="500016", landmark="Old Airport"),
    12: LocationNode(location_id=12, location_name="Mehdipatnam (Sarojini Devi)", latitude=17.3916, longitude=78.4414, area_code="500028", landmark="PVNR Flyover Entry"),
    13: LocationNode(location_id=13, location_name="Shamshabad (RGIA Airport)", latitude=17.2403, longitude=78.4294, area_code="500409", landmark="International Terminal"),
    14: LocationNode(location_id=14, location_name="Uppal (Cricket Stadium)", latitude=17.4018, longitude=78.5602, area_code="500039", landmark="RGIA Stadium"),
    15: LocationNode(location_id=15, location_name="Dilsukhnagar (Bus Station)", latitude=17.3688, longitude=78.5247, area_code="500060", landmark="Bus Terminal"),
    16: LocationNode(location_id=16, location_name="Charminar (Historic Old City)", latitude=17.3616, longitude=78.4747, area_code="500002", landmark="Heritage Precinct"),
}

# Default interconnected road network (matches seed.sql)
HYDERABAD_DEFAULT_ROADS: List[RoadEdge] = [
    RoadEdge(road_id=1, road_name="Cyber Towers - Mindspace Link", road_code="HYD-RD-01", start_location_id=1, end_location_id=2, distance_km=2.50, speed_limit_kmh=50, congestion_level=CongestionLevel.MODERATE),
    RoadEdge(road_id=2, road_name="Road No. 36 Madhapur - Jubilee Hills", road_code="HYD-RD-02", start_location_id=2, end_location_id=5, distance_km=4.20, speed_limit_kmh=50, congestion_level=CongestionLevel.HEAVY),
    RoadEdge(road_id=3, road_name="Road No. 10 Jubilee - Banjara Link", road_code="HYD-RD-03", start_location_id=5, end_location_id=6, distance_km=3.80, speed_limit_kmh=45, congestion_level=CongestionLevel.MODERATE),
    RoadEdge(road_id=4, road_name="Banjara Hills Rd 1 to Panjagutta", road_code="HYD-RD-04", start_location_id=6, end_location_id=7, distance_km=2.10, speed_limit_kmh=40, congestion_level=CongestionLevel.SEVERE, active_incidents=1, impact_delay_minutes=25),
    RoadEdge(road_id=5, road_name="Panjagutta - Ameerpet Corridor (NH-65)", road_code="HYD-RD-05", start_location_id=7, end_location_id=8, distance_km=2.30, speed_limit_kmh=45, congestion_level=CongestionLevel.SEVERE, active_incidents=1, impact_delay_minutes=20),
    RoadEdge(road_id=6, road_name="Ameerpet - Kukatpally Highway (NH-65)", road_code="HYD-RD-06", start_location_id=8, end_location_id=9, distance_km=7.50, speed_limit_kmh=60, congestion_level=CongestionLevel.HEAVY),
    RoadEdge(road_id=7, road_name="KPHB - Hitec City MMTS Link Road", road_code="HYD-RD-07", start_location_id=9, end_location_id=1, distance_km=5.80, speed_limit_kmh=50, congestion_level=CongestionLevel.LOW),
    RoadEdge(road_id=8, road_name="Panjagutta - Begumpet (Raj Bhavan Rd)", road_code="HYD-RD-08", start_location_id=7, end_location_id=11, distance_km=3.20, speed_limit_kmh=50, congestion_level=CongestionLevel.MODERATE),
    RoadEdge(road_id=9, road_name="Begumpet - Secunderabad (SP Road)", road_code="HYD-RD-09", start_location_id=11, end_location_id=10, distance_km=4.60, speed_limit_kmh=50, congestion_level=CongestionLevel.HEAVY),
    RoadEdge(road_id=10, road_name="Hitec City - Gachibowli Main Rd", road_code="HYD-RD-10", start_location_id=1, end_location_id=3, distance_km=4.80, speed_limit_kmh=60, congestion_level=CongestionLevel.HEAVY, active_incidents=1, impact_delay_minutes=15),
    RoadEdge(road_id=11, road_name="Gachibowli - Financial District (ISB Rd)", road_code="HYD-RD-11", start_location_id=3, end_location_id=4, distance_km=3.50, speed_limit_kmh=60, congestion_level=CongestionLevel.LOW),
    RoadEdge(road_id=12, road_name="Nehru ORR Expressway (Gachibowli - Shamshabad)", road_code="HYD-RD-12", start_location_id=3, end_location_id=13, distance_km=28.50, speed_limit_kmh=100, congestion_level=CongestionLevel.LOW, road_type="EXPRESSWAY"),
    RoadEdge(road_id=13, road_name="Nehru ORR (Financial District - Airport)", road_code="HYD-RD-13", start_location_id=4, end_location_id=13, distance_km=26.00, speed_limit_kmh=100, congestion_level=CongestionLevel.LOW, road_type="EXPRESSWAY"),
    RoadEdge(road_id=14, road_name="PVNR Elevated Expressway (Mehdipatnam - Airport)", road_code="HYD-RD-14", start_location_id=12, end_location_id=13, distance_km=21.00, speed_limit_kmh=80, congestion_level=CongestionLevel.MODERATE, road_type="EXPRESSWAY"),
    RoadEdge(road_id=15, road_name="Banjara Hills Rd 12 to Mehdipatnam", road_code="HYD-RD-15", start_location_id=6, end_location_id=12, distance_km=3.90, speed_limit_kmh=45, congestion_level=CongestionLevel.MODERATE),
    RoadEdge(road_id=16, road_name="Secunderabad - Uppal Corridor (via Habsiguda)", road_code="HYD-RD-16", start_location_id=10, end_location_id=14, distance_km=9.20, speed_limit_kmh=50, congestion_level=CongestionLevel.HEAVY),
    RoadEdge(road_id=17, road_name="Uppal - Dilsukhnagar Inner Ring Road", road_code="HYD-RD-17", start_location_id=14, end_location_id=15, distance_km=7.80, speed_limit_kmh=50, congestion_level=CongestionLevel.MODERATE),
    RoadEdge(road_id=18, road_name="Dilsukhnagar - Charminar (Chaderghat)", road_code="HYD-RD-18", start_location_id=15, end_location_id=16, distance_km=5.20, speed_limit_kmh=40, congestion_level=CongestionLevel.SEVERE, active_incidents=1, impact_delay_minutes=30),
    RoadEdge(road_id=19, road_name="Charminar - Mehdipatnam Link", road_code="HYD-RD-19", start_location_id=16, end_location_id=12, distance_km=6.40, speed_limit_kmh=40, congestion_level=CongestionLevel.HEAVY),
    RoadEdge(road_id=20, road_name="Secunderabad - Ameerpet Metro Line", road_code="HYD-RD-20", start_location_id=10, end_location_id=8, distance_km=6.50, speed_limit_kmh=50, congestion_level=CongestionLevel.MODERATE),
]


class RoadNetworkGraph:
    """Graph adjacency representation for shortest path and heuristic search."""

    def __init__(self, roads: List[RoadEdge] = None, locations: Dict[int, LocationNode] = None):
        self.locations = locations or HYDERABAD_LOCATIONS
        self.roads = roads or HYDERABAD_DEFAULT_ROADS
        self.adjacency: Dict[int, List[Tuple[int, RoadEdge]]] = {}
        self._build_adjacency()

    def _build_adjacency(self):
        for loc_id in self.locations:
            self.adjacency[loc_id] = []

        for road in self.roads:
            u = road.start_location_id
            v = road.end_location_id
            if u not in self.adjacency:
                self.adjacency[u] = []
            if v not in self.adjacency:
                self.adjacency[v] = []

            self.adjacency[u].append((v, road))
            if road.is_bidirectional:
                self.adjacency[v].append((u, road))

    def get_neighbors(self, node_id: int) -> List[Tuple[int, RoadEdge]]:
        return self.adjacency.get(node_id, [])

    def get_location(self, node_id: int) -> LocationNode:
        return self.locations.get(node_id)
