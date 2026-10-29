/**
 * TRAFFICIQ — Universal Persistent Data Store & Hybrid API Synchronizer
 * 
 * Works 100% seamlessly on ANY operating system:
 * 1. If Java/Python backend is running: Syncs with REST APIs.
 * 2. If running standalone / offline / file://: Uses reactive LocalStorage
 *    with full CRUD, automated triggers, and state persistence!
 */

const TrafficiQStore = {
    KEYS: {
        LOCATIONS: "trafficiq_db_locations",
        ROADS: "trafficiq_db_roads",
        INCIDENTS: "trafficiq_db_incidents",
        SNAPSHOTS: "trafficiq_db_snapshots"
    },

    // Initialize baseline Hyderabad database in localStorage if empty
    init() {
        if (!localStorage.getItem(this.KEYS.LOCATIONS)) {
            localStorage.setItem(this.KEYS.LOCATIONS, JSON.stringify(MOCK_HYDERABAD_LOCATIONS));
        }
        if (!localStorage.getItem(this.KEYS.ROADS)) {
            localStorage.setItem(this.KEYS.ROADS, JSON.stringify(MOCK_HYDERABAD_ROADS));
        }
        if (!localStorage.getItem(this.KEYS.INCIDENTS)) {
            localStorage.setItem(this.KEYS.INCIDENTS, JSON.stringify(MOCK_HYDERABAD_INCIDENTS));
        }
    },

    // ------------------------------------------------------------------------
    // LOCATIONS
    // ------------------------------------------------------------------------
    async getLocations() {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/locations`);
            if (res.ok) {
                const json = await res.json();
                if (json.success && json.data && json.data.length > 0) {
                    localStorage.setItem(this.KEYS.LOCATIONS, JSON.stringify(json.data));
                    return json.data;
                }
            }
        } catch (e) {
            // Backend offline - proceed with local store
        }
        this.init();
        return JSON.parse(localStorage.getItem(this.KEYS.LOCATIONS)) || MOCK_HYDERABAD_LOCATIONS;
    },

    // ------------------------------------------------------------------------
    // ROADS CRUD
    // ------------------------------------------------------------------------
    async getRoads() {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/roads`);
            if (res.ok) {
                const json = await res.json();
                if (json.success && json.data && json.data.length > 0) {
                    localStorage.setItem(this.KEYS.ROADS, JSON.stringify(json.data));
                    return json.data;
                }
            }
        } catch (e) {}
        this.init();
        return JSON.parse(localStorage.getItem(this.KEYS.ROADS)) || MOCK_HYDERABAD_ROADS;
    },

    async addRoad(roadData) {
        // Try Backend first
        try {
            const res = await fetch(`${CONFIG.API_BASE}/roads`, {
                method: "POST",
                headers: { "Content-Type": "application/json", ...Auth.getAuthHeaders() },
                body: JSON.stringify(roadData)
            });
            if (res.ok) {
                const json = await res.json();
                if (json.success && json.data) {
                    await this.getRoads(); // Refresh store
                    return { success: true, data: json.data };
                }
            }
        } catch (e) {}

        // Local Store Fallback
        this.init();
        const roads = JSON.parse(localStorage.getItem(this.KEYS.ROADS)) || [];
        const locations = await this.getLocations();

        const startLoc = locations.find(l => (l.location_id || l.locationId) == roadData.startLocationId) || {};
        const endLoc = locations.find(l => (l.location_id || l.locationId) == roadData.endLocationId) || {};

        const newId = roads.length > 0 ? Math.max(...roads.map(r => r.road_id || r.roadId || 0)) + 1 : 21;
        const speed = roadData.speedLimitKmh || 50;
        const dist = parseFloat(roadData.distanceKm) || 4.0;
        const estTime = Math.round((dist / speed) * 60 * 10) / 10;

        const newRoad = {
            road_id: newId,
            roadId: newId,
            road_code: roadData.roadCode,
            roadCode: roadData.roadCode,
            road_name: roadData.roadName,
            roadName: roadData.roadName,
            road_type: roadData.roadType || "ARTERIAL",
            roadType: roadData.roadType || "ARTERIAL",
            start_location_name: startLoc.location_name || "Hyderabad Junction",
            startLocationName: startLoc.location_name || "Hyderabad Junction",
            end_location_name: endLoc.location_name || "Hyderabad Junction",
            endLocationName: endLoc.location_name || "Hyderabad Junction",
            start_lat: startLoc.latitude || 17.43,
            startLatitude: startLoc.latitude || 17.43,
            start_lng: startLoc.longitude || 78.44,
            startLongitude: startLoc.longitude || 78.44,
            end_lat: endLoc.latitude || 17.44,
            endLatitude: endLoc.latitude || 17.44,
            end_lng: endLoc.longitude || 78.45,
            endLongitude: endLoc.longitude || 78.45,
            distance_km: dist,
            distanceKm: dist,
            speed_limit_kmh: speed,
            speedLimitKmh: speed,
            current_speed_kmh: speed,
            currentSpeedKmh: speed,
            congestion_level: "LOW",
            congestionLevel: "LOW",
            estimated_travel_time_min: estTime,
            estimatedTravelTimeMin: estTime,
            active_incidents: 0,
            activeIncidents: 0,
            lanes: roadData.lanes || 4,
            is_toll: !!roadData.isToll,
            is_bidirectional: roadData.isBidirectional !== false
        };

        roads.unshift(newRoad);
        localStorage.setItem(this.KEYS.ROADS, JSON.stringify(roads));
        return { success: true, data: newRoad };
    },

    async updateRoad(id, roadData) {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/roads/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json", ...Auth.getAuthHeaders() },
                body: JSON.stringify(roadData)
            });
            if (res.ok) {
                await this.getRoads();
                return { success: true };
            }
        } catch (e) {}

        this.init();
        const roads = JSON.parse(localStorage.getItem(this.KEYS.ROADS)) || [];
        const idx = roads.findIndex(r => (r.road_id || r.roadId) == id);
        if (idx !== -1) {
            roads[idx].road_name = roadData.roadName || roads[idx].road_name;
            roads[idx].distance_km = parseFloat(roadData.distanceKm) || roads[idx].distance_km;
            roads[idx].speed_limit_kmh = parseInt(roadData.speedLimitKmh) || roads[idx].speed_limit_kmh;
            roads[idx].road_type = roadData.roadType || roads[idx].road_type;
            localStorage.setItem(this.KEYS.ROADS, JSON.stringify(roads));
            return { success: true, data: roads[idx] };
        }
        return { success: false, message: "Road not found." };
    },

    async deleteRoad(id) {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/roads/${id}`, {
                method: "DELETE",
                headers: Auth.getAuthHeaders()
            });
            if (res.ok) {
                await this.getRoads();
                return { success: true };
            }
        } catch (e) {}

        this.init();
        let roads = JSON.parse(localStorage.getItem(this.KEYS.ROADS)) || [];
        roads = roads.filter(r => (r.road_id || r.roadId) != id);
        localStorage.setItem(this.KEYS.ROADS, JSON.stringify(roads));
        return { success: true };
    },

    // ------------------------------------------------------------------------
    // INCIDENTS CRUD (with automated trigger simulation)
    // ------------------------------------------------------------------------
    async getIncidents() {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/incidents`);
            if (res.ok) {
                const json = await res.json();
                if (json.success && json.data && json.data.length > 0) {
                    localStorage.setItem(this.KEYS.INCIDENTS, JSON.stringify(json.data));
                    return json.data;
                }
            }
        } catch (e) {}
        this.init();
        return JSON.parse(localStorage.getItem(this.KEYS.INCIDENTS)) || MOCK_HYDERABAD_INCIDENTS;
    },

    async addIncident(incData) {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/incidents`, {
                method: "POST",
                headers: { "Content-Type": "application/json", ...Auth.getAuthHeaders() },
                body: JSON.stringify(incData)
            });
            if (res.ok) {
                const json = await res.json();
                if (json.success && json.data) {
                    await this.getIncidents();
                    return { success: true, data: json.data };
                }
            }
        } catch (e) {}

        this.init();
        const incidents = JSON.parse(localStorage.getItem(this.KEYS.INCIDENTS)) || [];
        const roads = JSON.parse(localStorage.getItem(this.KEYS.ROADS)) || [];
        const road = roads.find(r => (r.road_id || r.roadId) == incData.roadId) || {};

        const newId = incidents.length > 0 ? Math.max(...incidents.map(i => i.incident_id || i.incidentId || 0)) + 1 : 6;
        const newInc = {
            incident_id: newId,
            incidentId: newId,
            road_id: incData.roadId,
            roadId: incData.roadId,
            road_name: road.road_name || "Hyderabad Corridor",
            roadName: road.road_name || "Hyderabad Corridor",
            incident_type: incData.incidentType,
            incidentType: incData.incidentType,
            severity: incData.severity,
            description: incData.description,
            status: "REPORTED",
            reported_by_name: Auth.getUser() ? Auth.getUser().name : "Public Traffic Monitor",
            reported_at: "Just now",
            impact_delay_minutes: parseInt(incData.impactDelayMinutes) || 20,
            latitude: road.start_lat ? (road.start_lat + road.end_lat) / 2 : 17.43,
            longitude: road.start_lng ? (road.start_lng + road.end_lng) / 2 : 78.44
        };

        incidents.unshift(newInc);
        localStorage.setItem(this.KEYS.INCIDENTS, JSON.stringify(incidents));

        // Automated Trigger Simulation: Degrade road traffic condition
        const roadIdx = roads.findIndex(r => (r.road_id || r.roadId) == incData.roadId);
        if (roadIdx !== -1) {
            const isCrit = incData.severity === "CRITICAL";
            roads[roadIdx].congestion_level = isCrit ? "SEVERE" : (incData.severity === "HIGH" ? "HEAVY" : "MODERATE");
            roads[roadIdx].congestionLevel = roads[roadIdx].congestion_level;
            roads[roadIdx].current_speed_kmh = isCrit ? 12.0 : 22.0;
            roads[roadIdx].currentSpeedKmh = roads[roadIdx].current_speed_kmh;
            roads[roadIdx].active_incidents = (roads[roadIdx].active_incidents || 0) + 1;
            roads[roadIdx].activeIncidents = roads[roadIdx].active_incidents;
            localStorage.setItem(this.KEYS.ROADS, JSON.stringify(roads));
        }

        return { success: true, data: newInc };
    },

    async resolveIncident(id) {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/incidents/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json", ...Auth.getAuthHeaders() },
                body: JSON.stringify({ status: "RESOLVED" })
            });
            if (res.ok) {
                await this.getIncidents();
                return { success: true };
            }
        } catch (e) {}

        this.init();
        const incidents = JSON.parse(localStorage.getItem(this.KEYS.INCIDENTS)) || [];
        const inc = incidents.find(i => (i.incident_id || i.incidentId) == id);
        if (inc) {
            inc.status = "RESOLVED";
            localStorage.setItem(this.KEYS.INCIDENTS, JSON.stringify(incidents));

            // Automated Trigger Simulation: Restore normal road flow
            const roads = JSON.parse(localStorage.getItem(this.KEYS.ROADS)) || [];
            const roadIdx = roads.findIndex(r => (r.road_id || r.roadId) == (inc.road_id || inc.roadId));
            if (roadIdx !== -1) {
                roads[roadIdx].congestion_level = "LOW";
                roads[roadIdx].congestionLevel = "LOW";
                roads[roadIdx].current_speed_kmh = Math.round(roads[roadIdx].speed_limit_kmh * 0.85);
                roads[roadIdx].currentSpeedKmh = roads[roadIdx].current_speed_kmh;
                roads[roadIdx].active_incidents = Math.max(0, (roads[roadIdx].active_incidents || 1) - 1);
                roads[roadIdx].activeIncidents = roads[roadIdx].active_incidents;
                localStorage.setItem(this.KEYS.ROADS, JSON.stringify(roads));
            }
            return { success: true };
        }
        return { success: false, message: "Incident not found." };
    },

    async deleteIncident(id) {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/incidents/${id}`, {
                method: "DELETE",
                headers: Auth.getAuthHeaders()
            });
            if (res.ok) {
                await this.getIncidents();
                return { success: true };
            }
        } catch (e) {}

        this.init();
        let incidents = JSON.parse(localStorage.getItem(this.KEYS.INCIDENTS)) || [];
        incidents = incidents.filter(i => (i.incident_id || i.incidentId) != id);
        localStorage.setItem(this.KEYS.INCIDENTS, JSON.stringify(incidents));
        return { success: true };
    }
};

// Auto initialize on load
TrafficiQStore.init();
