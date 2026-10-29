/**
 * TRAFFICIQ — Frontend Configuration & Fallback Hyderabad Dataset
 */

const CONFIG = {
    // Backend API endpoints (Spring Boot 8080)
    API_BASE: "http://localhost:8080/api",
    PYTHON_API_BASE: "http://localhost:8000",

    // Default Hyderabad Map Coordinates
    MAP_CENTER: [17.3850, 78.4867],
    MAP_DEFAULT_ZOOM: 12,

    // Storage Keys
    THEME_STORAGE_KEY: "trafficiq_theme",
    TOKEN_STORAGE_KEY: "trafficiq_auth_token",
    USER_STORAGE_KEY: "trafficiq_user"
};

// Realistic Hyderabad Locations for instant map rendering and autocomplete fallback
const MOCK_HYDERABAD_LOCATIONS = [
    { location_id: 1, location_name: "HITEC City (Cyber Towers)", latitude: 17.4504, longitude: 78.3808, landmark: "Cyber Towers Junction" },
    { location_id: 2, location_name: "Madhapur (Mindspace)", latitude: 17.4399, longitude: 78.3815, landmark: "Mindspace IT Park" },
    { location_id: 3, location_name: "Gachibowli (Bio-Diversity)", latitude: 17.4401, longitude: 78.3489, landmark: "Bio-Diversity Flyover" },
    { location_id: 4, location_name: "Financial District (Nanakramguda)", latitude: 17.4156, longitude: 78.3427, landmark: "WaveRock SEZ" },
    { location_id: 5, location_name: "Jubilee Hills (Check Post / Rd 36)", latitude: 17.4319, longitude: 78.4073, landmark: "Check Post Metro" },
    { location_id: 6, location_name: "Banjara Hills (Road No. 1)", latitude: 17.4156, longitude: 78.4354, landmark: "City Centre Mall" },
    { location_id: 7, location_name: "Panjagutta Junction", latitude: 17.4265, longitude: 78.4526, landmark: "Panjagutta Central & Flyover" },
    { location_id: 8, location_name: "Ameerpet (Metro Interchange)", latitude: 17.4375, longitude: 78.4482, landmark: "Ameerpet Metro Junction" },
    { location_id: 9, location_name: "Kukatpally (KPHB Colony)", latitude: 17.4938, longitude: 78.3995, landmark: "JNTU / Forum Mall" },
    { location_id: 10, location_name: "Secunderabad (Railway Station)", latitude: 17.4399, longitude: 78.4983, landmark: "Secunderabad Terminal" },
    { location_id: 11, location_name: "Begumpet (Airport Road)", latitude: 17.4448, longitude: 78.4688, landmark: "Old Airport" },
    { location_id: 12, location_name: "Mehdipatnam (Sarojini Devi)", latitude: 17.3916, longitude: 78.4414, landmark: "PVNR Flyover Entry" },
    { location_id: 13, location_name: "Shamshabad (RGIA Airport)", latitude: 17.2403, longitude: 78.4294, landmark: "Rajiv Gandhi Airport" },
    { location_id: 14, location_name: "Uppal (Cricket Stadium)", latitude: 17.4018, longitude: 78.5602, landmark: "RGIA Stadium" },
    { location_id: 15, location_name: "Dilsukhnagar (Bus Station)", latitude: 17.3688, longitude: 78.5247, landmark: "Metro Terminal" },
    { location_id: 16, location_name: "Charminar (Historic Old City)", latitude: 17.3616, longitude: 78.4747, landmark: "Charminar Heritage" }
];

// Fallback Hyderabad Road Segments with Realistic Telemetry
const MOCK_HYDERABAD_ROADS = [
    { road_id: 1, road_code: "HYD-RD-01", road_name: "Cyber Towers - Mindspace Link", start_location_name: "HITEC City (Cyber Towers)", end_location_name: "Madhapur (Mindspace)", start_lat: 17.4504, start_lng: 78.3808, end_lat: 17.4399, end_lng: 78.3815, road_type: "ARTERIAL", distance_km: 2.50, speed_limit_kmh: 50, current_speed_kmh: 34.50, congestion_level: "MODERATE", estimated_travel_time_min: 4.35, active_incidents: 0 },
    { road_id: 2, road_code: "HYD-RD-02", road_name: "Road No. 36 Madhapur - Jubilee Hills", start_location_name: "Madhapur (Mindspace)", end_location_name: "Jubilee Hills (Check Post / Rd 36)", start_lat: 17.4399, start_lng: 78.3815, end_lat: 17.4319, end_lng: 78.4073, road_type: "ARTERIAL", distance_km: 4.20, speed_limit_kmh: 50, current_speed_kmh: 21.00, congestion_level: "HEAVY", estimated_travel_time_min: 12.00, active_incidents: 1 },
    { road_id: 3, road_code: "HYD-RD-03", road_name: "Road No. 10 Jubilee - Banjara Link", start_location_name: "Jubilee Hills (Check Post / Rd 36)", end_location_name: "Banjara Hills (Road No. 1)", start_lat: 17.4319, start_lng: 78.4073, end_lat: 17.4156, end_lng: 78.4354, road_type: "ARTERIAL", distance_km: 3.80, speed_limit_kmh: 45, current_speed_kmh: 32.00, congestion_level: "MODERATE", estimated_travel_time_min: 7.12, active_incidents: 0 },
    { road_id: 4, road_code: "HYD-RD-04", road_name: "Banjara Hills Rd 1 to Panjagutta", start_location_name: "Banjara Hills (Road No. 1)", end_location_name: "Panjagutta Junction", start_lat: 17.4156, start_lng: 78.4354, end_lat: 17.4265, end_lng: 78.4526, road_type: "ARTERIAL", distance_km: 2.10, speed_limit_kmh: 40, current_speed_kmh: 12.50, congestion_level: "SEVERE", estimated_travel_time_min: 10.08, active_incidents: 1 },
    { road_id: 5, road_code: "HYD-RD-05", road_name: "Panjagutta - Ameerpet Corridor (NH-65)", start_location_name: "Panjagutta Junction", end_location_name: "Ameerpet (Metro Interchange)", start_lat: 17.4265, start_lng: 78.4526, end_lat: 17.4375, end_lng: 78.4482, road_type: "ARTERIAL", distance_km: 2.30, speed_limit_kmh: 45, current_speed_kmh: 14.00, congestion_level: "SEVERE", estimated_travel_time_min: 9.85, active_incidents: 1 },
    { road_id: 6, road_code: "HYD-RD-06", road_name: "Ameerpet - Kukatpally Highway (NH-65)", start_location_name: "Ameerpet (Metro Interchange)", end_location_name: "Kukatpally (KPHB Colony)", start_lat: 17.4375, start_lng: 78.4482, end_lat: 17.4938, end_lng: 78.3995, road_type: "ARTERIAL", distance_km: 7.50, speed_limit_kmh: 60, current_speed_kmh: 28.00, congestion_level: "HEAVY", estimated_travel_time_min: 16.07, active_incidents: 0 },
    { road_id: 7, road_code: "HYD-RD-07", road_name: "KPHB - Hitec City MMTS Link Road", start_location_name: "Kukatpally (KPHB Colony)", end_location_name: "HITEC City (Cyber Towers)", start_lat: 17.4938, start_lng: 78.3995, end_lat: 17.4504, end_lng: 78.3808, road_type: "SUB_ARTERIAL", distance_km: 5.80, speed_limit_kmh: 50, current_speed_kmh: 46.00, congestion_level: "LOW", estimated_travel_time_min: 7.56, active_incidents: 0 },
    { road_id: 8, road_code: "HYD-RD-08", road_name: "Panjagutta - Begumpet (Raj Bhavan Rd)", start_location_name: "Panjagutta Junction", end_location_name: "Begumpet (Airport Road)", start_lat: 17.4265, start_lng: 78.4526, end_lat: 17.4448, end_lng: 78.4688, road_type: "ARTERIAL", distance_km: 3.20, speed_limit_kmh: 50, current_speed_kmh: 35.00, congestion_level: "MODERATE", estimated_travel_time_min: 5.48, active_incidents: 0 },
    { road_id: 9, road_code: "HYD-RD-09", road_name: "Begumpet - Secunderabad (SP Road)", start_location_name: "Begumpet (Airport Road)", end_location_name: "Secunderabad (Railway Station)", start_lat: 17.4448, start_lng: 78.4688, end_lat: 17.4399, end_lng: 78.4983, road_type: "ARTERIAL", distance_km: 4.60, speed_limit_kmh: 50, current_speed_kmh: 22.50, congestion_level: "HEAVY", estimated_travel_time_min: 12.26, active_incidents: 0 },
    { road_id: 10, road_code: "HYD-RD-10", road_name: "Hitec City - Gachibowli Main Rd", start_location_name: "HITEC City (Cyber Towers)", end_location_name: "Gachibowli (Bio-Diversity)", start_lat: 17.4504, start_lng: 78.3808, end_lat: 17.4401, end_lng: 78.3489, road_type: "ARTERIAL", distance_km: 4.80, speed_limit_kmh: 60, current_speed_kmh: 24.00, congestion_level: "HEAVY", estimated_travel_time_min: 12.00, active_incidents: 1 },
    { road_id: 11, road_code: "HYD-RD-11", road_name: "Gachibowli - Financial District (ISB Rd)", start_location_name: "Gachibowli (Bio-Diversity)", end_location_name: "Financial District (Nanakramguda)", start_lat: 17.4401, start_lng: 78.3489, end_lat: 17.4156, end_lng: 78.3427, road_type: "ARTERIAL", distance_km: 3.50, speed_limit_kmh: 60, current_speed_kmh: 52.00, congestion_level: "LOW", estimated_travel_time_min: 4.04, active_incidents: 0 },
    { road_id: 12, road_code: "HYD-RD-12", road_name: "Nehru ORR Expressway (Gachibowli - Shamshabad)", start_location_name: "Gachibowli (Bio-Diversity)", end_location_name: "Shamshabad (RGIA Airport)", start_lat: 17.4401, start_lng: 78.3489, end_lat: 17.2403, end_lng: 78.4294, road_type: "EXPRESSWAY", distance_km: 28.50, speed_limit_kmh: 100, current_speed_kmh: 92.00, congestion_level: "LOW", estimated_travel_time_min: 18.58, active_incidents: 0 },
    { road_id: 13, road_code: "HYD-RD-13", road_name: "Nehru ORR (Financial District - Airport)", start_location_name: "Financial District (Nanakramguda)", end_location_name: "Shamshabad (RGIA Airport)", start_lat: 17.4156, start_lng: 78.3427, end_lat: 17.2403, end_lng: 78.4294, road_type: "EXPRESSWAY", distance_km: 26.00, speed_limit_kmh: 100, current_speed_kmh: 94.00, congestion_level: "LOW", estimated_travel_time_min: 16.60, active_incidents: 0 },
    { road_id: 14, road_code: "HYD-RD-14", road_name: "PVNR Elevated Expressway (Mehdipatnam - Airport)", start_location_name: "Mehdipatnam (Sarojini Devi)", end_location_name: "Shamshabad (RGIA Airport)", start_lat: 17.3916, start_lng: 78.4414, end_lat: 17.2403, end_lng: 78.4294, road_type: "EXPRESSWAY", distance_km: 21.00, speed_limit_kmh: 80, current_speed_kmh: 62.00, congestion_level: "MODERATE", estimated_travel_time_min: 20.32, active_incidents: 0 },
    { road_id: 15, road_code: "HYD-RD-15", road_name: "Banjara Hills Rd 12 to Mehdipatnam", start_location_name: "Banjara Hills (Road No. 1)", end_location_name: "Mehdipatnam (Sarojini Devi)", start_lat: 17.4156, start_lng: 78.4354, end_lat: 17.3916, end_lng: 78.4414, road_type: "ARTERIAL", distance_km: 3.90, speed_limit_kmh: 45, current_speed_kmh: 30.00, congestion_level: "MODERATE", estimated_travel_time_min: 7.80, active_incidents: 0 },
    { road_id: 16, road_code: "HYD-RD-16", road_name: "Secunderabad - Uppal Corridor (via Habsiguda)", start_location_name: "Secunderabad (Railway Station)", end_location_name: "Uppal (Cricket Stadium)", start_lat: 17.4399, start_lng: 78.4983, end_lat: 17.4018, end_lng: 78.5602, road_type: "ARTERIAL", distance_km: 9.20, speed_limit_kmh: 50, current_speed_kmh: 25.00, congestion_level: "HEAVY", estimated_travel_time_min: 22.08, active_incidents: 0 },
    { road_id: 17, road_code: "HYD-RD-17", road_name: "Uppal - Dilsukhnagar Inner Ring Road", start_location_name: "Uppal (Cricket Stadium)", end_location_name: "Dilsukhnagar (Bus Station)", start_lat: 17.4018, start_lng: 78.5602, end_lat: 17.3688, end_lng: 78.5247, road_type: "ARTERIAL", distance_km: 7.80, speed_limit_kmh: 50, current_speed_kmh: 33.00, congestion_level: "MODERATE", estimated_travel_time_min: 14.18, active_incidents: 0 },
    { road_id: 18, road_code: "HYD-RD-18", road_name: "Dilsukhnagar - Charminar (Chaderghat)", start_location_name: "Dilsukhnagar (Bus Station)", end_location_name: "Charminar (Historic Old City)", start_lat: 17.3688, start_lng: 78.5247, end_lat: 17.3616, end_lng: 78.4747, road_type: "SUB_ARTERIAL", distance_km: 5.20, speed_limit_kmh: 40, current_speed_kmh: 11.00, congestion_level: "SEVERE", estimated_travel_time_min: 28.36, active_incidents: 1 },
    { road_id: 19, road_code: "HYD-RD-19", road_name: "Charminar - Mehdipatnam Link", start_location_name: "Charminar (Historic Old City)", end_location_name: "Mehdipatnam (Sarojini Devi)", start_lat: 17.3616, start_lng: 78.4747, end_lat: 17.3916, end_lng: 78.4414, road_type: "SUB_ARTERIAL", distance_km: 6.40, speed_limit_kmh: 40, current_speed_kmh: 18.00, congestion_level: "HEAVY", estimated_travel_time_min: 21.33, active_incidents: 0 },
    { road_id: 20, road_code: "HYD-RD-20", road_name: "Secunderabad - Ameerpet Metro Line", start_location_name: "Secunderabad (Railway Station)", end_location_name: "Ameerpet (Metro Interchange)", start_lat: 17.4399, start_lng: 78.4983, end_lat: 17.4375, end_lng: 78.4482, road_type: "ARTERIAL", distance_km: 6.50, speed_limit_kmh: 50, current_speed_kmh: 31.00, congestion_level: "MODERATE", estimated_travel_time_min: 12.58, active_incidents: 0 }
];

// Fallback Incidents
const MOCK_HYDERABAD_INCIDENTS = [
    { incident_id: 1, road_id: 4, road_name: "Banjara Hills Rd 1 to Panjagutta", incident_type: "ACCIDENT", severity: "CRITICAL", description: "Multi-vehicle collision near Panjagutta Central Mall flyover ascent.", status: "IN_PROGRESS", reported_by_name: "System Administrator", reported_at: "45 mins ago", impact_delay_minutes: 25, latitude: 17.4210, longitude: 78.4440 },
    { incident_id: 2, road_id: 5, road_name: "Panjagutta - Ameerpet Corridor (NH-65)", incident_type: "CONSTRUCTION", severity: "HIGH", description: "Hyderabad Metro Rail track inspection. Single lane operational.", status: "REPORTED", reported_by_name: "System Administrator", reported_at: "2 hours ago", impact_delay_minutes: 20, latitude: 17.4320, longitude: 78.4504 },
    { incident_id: 3, road_id: 10, road_name: "Hitec City - Gachibowli Main Rd", incident_type: "WATERLOGGING", severity: "MEDIUM", description: "Water accumulation near Bio-Diversity Park junction causing slow vehicular movement.", status: "REPORTED", reported_by_name: "Demo Commuter", reported_at: "1 hour ago", impact_delay_minutes: 15, latitude: 17.4452, longitude: 78.3648 },
    { incident_id: 4, road_id: 18, road_name: "Dilsukhnagar - Charminar (Chaderghat)", incident_type: "ROAD_CLOSURE", severity: "HIGH", description: "Heritage street pedestrianization trial run near Charminar.", status: "IN_PROGRESS", reported_by_name: "System Administrator", reported_at: "3 hours ago", impact_delay_minutes: 30, latitude: 17.3652, longitude: 78.4997 },
    { incident_id: 5, road_id: 2, road_name: "Road No. 36 Madhapur - Jubilee Hills", incident_type: "BREAKDOWN", severity: "LOW", description: "RTC City Bus breakdown on Road No. 36 incline towards Jubilee Hills.", status: "REPORTED", reported_by_name: "Demo Commuter", reported_at: "20 mins ago", impact_delay_minutes: 10, latitude: 17.4359, longitude: 78.3944 }
];

// Universal Exports
if (typeof module !== "undefined" && module.exports) {
    module.exports = {
        CONFIG,
        MOCK_HYDERABAD_LOCATIONS,
        MOCK_HYDERABAD_ROADS,
        MOCK_HYDERABAD_INCIDENTS
    };
}
