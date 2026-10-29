-- ============================================================================
-- TRAFFICIQ: Seed Data (Hyderabad Metropolitan Road Network & Real-World Corridors)
-- DBMS Syllabus Implementation: Units I - V
-- File: database/seed.sql
-- ============================================================================

USE trafficiq_db;

-- ----------------------------------------------------------------------------
-- 1. Insert Initial Users (Admin and Standard User)
-- Default passwords:
-- Admin: admin@trafficiq.com / Admin@123
-- User:  user@trafficiq.com  / User@123
-- BCrypt hashes generated using standard 10-round salt
-- ----------------------------------------------------------------------------
INSERT INTO users (user_id, name, email, password_hash, role, phone, is_active) VALUES
(1, 'System Administrator', 'admin@trafficiq.com', '$2a$10$7zB3c4N9c9w4Zc5N6iY5E.o4r8tF0w6q4L5K6j7h8G9F0E1D2C3B4', 'ADMIN', '+91 98480 22338', TRUE),
(2, 'Demo Commuter', 'user@trafficiq.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', '+91 94400 11223', TRUE);

-- ----------------------------------------------------------------------------
-- 2. Insert Key Hyderabad Spatial Locations (Nodes in Road Network Graph)
-- Precise GPS Coordinates for mapping on Leaflet / Satellite tiles
-- ----------------------------------------------------------------------------
INSERT INTO locations (location_id, location_name, area_code, latitude, longitude, landmark, city) VALUES
(1, 'HITEC City (Cyber Towers)', 'TG-HYD-500081', 17.4504000, 78.3808000, 'Cyber Towers Junction', 'Hyderabad'),
(2, 'Madhapur (Mindspace)', 'TG-HYD-500081', 17.4399000, 78.3815000, 'Mindspace IT Park Entrance', 'Hyderabad'),
(3, 'Gachibowli (Bio-Diversity)', 'TG-HYD-500032', 17.4401000, 78.3489000, 'Bio-Diversity Flyover', 'Hyderabad'),
(4, 'Financial District (Nanakramguda)', 'TG-HYD-500032', 17.4156000, 78.3427000, 'WaveRock SEZ / US Consulate', 'Hyderabad'),
(5, 'Jubilee Hills (Check Post / Rd 36)', 'TG-HYD-500033', 17.4319000, 78.4073000, 'Jubilee Hills Check Post Metro', 'Hyderabad'),
(6, 'Banjara Hills (Road No. 1)', 'TG-HYD-500034', 17.4156000, 78.4354000, 'City Centre Mall / Taj Krishna', 'Hyderabad'),
(7, 'Panjagutta Junction', 'TG-HYD-500082', 17.4265000, 78.4526000, 'Panjagutta Central & Flyover', 'Hyderabad'),
(8, 'Ameerpet (Metro Interchange)', 'TG-HYD-500016', 17.4375000, 78.4482000, 'Ameerpet Metro Junction', 'Hyderabad'),
(9, 'Kukatpally (KPHB Colony)', 'TG-HYD-500072', 17.4938000, 78.3995000, 'JNTU / Forum Sujana Mall', 'Hyderabad'),
(10, 'Secunderabad (Railway Station)', 'TG-HYD-500003', 17.4399000, 78.4983000, 'Secunderabad Main Terminal', 'Hyderabad'),
(11, 'Begumpet (Airport Road)', 'TG-HYD-500016', 17.4448000, 78.4688000, 'Prakash Nagar / Old Airport', 'Hyderabad'),
(12, 'Mehdipatnam (Sarojini Devi)', 'TG-HYD-500028', 17.3916000, 78.4414000, 'PVNR Elevated Flyover Start', 'Hyderabad'),
(13, 'Shamshabad (RGIA Airport)', 'TG-HYD-500409', 17.2403000, 78.4294000, 'Rajiv Gandhi Int. Airport', 'Hyderabad'),
(14, 'Uppal (Cricket Stadium)', 'TG-HYD-500039', 17.4018000, 78.5602000, 'Rajiv Gandhi Int. Stadium', 'Hyderabad'),
(15, 'Dilsukhnagar (Bus Station)', 'TG-HYD-500060', 17.3688000, 78.5247000, 'Dilsukhnagar Metro Terminal', 'Hyderabad'),
(16, 'Charminar (Historic Old City)', 'TG-HYD-500002', 17.3616000, 78.4747000, 'Charminar Heritage Precinct', 'Hyderabad');

-- ----------------------------------------------------------------------------
-- 3. Insert Road Network Segments (Edges in Road Network Graph)
-- Connects Hyderabad locations into a fully navigable urban mesh
-- ----------------------------------------------------------------------------
INSERT INTO roads (road_id, road_name, road_code, start_location_id, end_location_id, road_type, distance_km, speed_limit_kmh, lanes, is_toll, is_bidirectional) VALUES
(1, 'Cyber Towers - Mindspace Link', 'HYD-RD-01', 1, 2, 'ARTERIAL', 2.50, 50, 6, FALSE, TRUE),
(2, 'Road No. 36 Madhapur - Jubilee Hills', 'HYD-RD-02', 2, 5, 'ARTERIAL', 4.20, 50, 4, FALSE, TRUE),
(3, 'Road No. 10 Jubilee - Banjara Link', 'HYD-RD-03', 5, 6, 'ARTERIAL', 3.80, 45, 4, FALSE, TRUE),
(4, 'Banjara Hills Rd 1 to Panjagutta', 'HYD-RD-04', 6, 7, 'ARTERIAL', 2.10, 40, 4, FALSE, TRUE),
(5, 'Panjagutta - Ameerpet Corridor (NH-65)', 'HYD-RD-05', 7, 8, 'ARTERIAL', 2.30, 45, 6, FALSE, TRUE),
(6, 'Ameerpet - Kukatpally Highway (NH-65)', 'HYD-RD-06', 8, 9, 'ARTERIAL', 7.50, 60, 6, FALSE, TRUE),
(7, 'KPHB - Hitec City MMTS Link Road', 'HYD-RD-07', 9, 1, 'SUB_ARTERIAL', 5.80, 50, 4, FALSE, TRUE),
(8, 'Panjagutta - Begumpet (Raj Bhavan Rd)', 'HYD-RD-08', 7, 11, 'ARTERIAL', 3.20, 50, 6, FALSE, TRUE),
(9, 'Begumpet - Secunderabad (SP Road)', 'HYD-RD-09', 11, 10, 'ARTERIAL', 4.60, 50, 6, FALSE, TRUE),
(10, 'Hitec City - Gachibowli Main Rd', 'HYD-RD-10', 1, 3, 'ARTERIAL', 4.80, 60, 6, FALSE, TRUE),
(11, 'Gachibowli - Financial District (ISB Rd)', 'HYD-RD-11', 3, 4, 'ARTERIAL', 3.50, 60, 6, FALSE, TRUE),
(12, 'Nehru ORR Expressway (Gachibowli - Shamshabad)', 'HYD-RD-12', 3, 13, 'EXPRESSWAY', 28.50, 100, 8, TRUE, TRUE),
(13, 'Nehru ORR (Financial District - Airport)', 'HYD-RD-13', 4, 13, 'EXPRESSWAY', 26.00, 100, 8, TRUE, TRUE),
(14, 'PVNR Elevated Expressway (Mehdipatnam - Airport)', 'HYD-RD-14', 12, 13, 'EXPRESSWAY', 21.00, 80, 4, FALSE, TRUE),
(15, 'Banjara Hills Rd 12 to Mehdipatnam', 'HYD-RD-15', 6, 12, 'ARTERIAL', 3.90, 45, 4, FALSE, TRUE),
(16, 'Secunderabad - Uppal Corridor (via Habsiguda)', 'HYD-RD-16', 10, 14, 'ARTERIAL', 9.20, 50, 6, FALSE, TRUE),
(17, 'Uppal - Dilsukhnagar Inner Ring Road', 'HYD-RD-17', 14, 15, 'ARTERIAL', 7.80, 50, 4, FALSE, TRUE),
(18, 'Dilsukhnagar - Charminar (Chaderghat)', 'HYD-RD-18', 15, 16, 'SUB_ARTERIAL', 5.20, 40, 4, FALSE, TRUE),
(19, 'Charminar - Mehdipatnam Link', 'HYD-RD-19', 16, 12, 'SUB_ARTERIAL', 6.40, 40, 4, FALSE, TRUE),
(20, 'Secunderabad - Ameerpet Metro Line', 'HYD-RD-20', 10, 8, 'ARTERIAL', 6.50, 50, 6, FALSE, TRUE);

-- ----------------------------------------------------------------------------
-- 4. Insert Initial Traffic Snapshots (Current & Realistic Conditions)
-- Demonstrating Low, Moderate, Heavy, and Severe levels
-- ----------------------------------------------------------------------------
INSERT INTO traffic_snapshots (snapshot_id, road_id, congestion_level, average_speed_kmh, vehicle_count, estimated_travel_time_min, recorded_at, data_source) VALUES
(1, 1, 'MODERATE', 34.50, 420, 4.35, NOW(), 'DEMO_SIMULATED'),
(2, 2, 'HEAVY', 21.00, 680, 12.00, NOW(), 'DEMO_SIMULATED'),
(3, 3, 'MODERATE', 32.00, 390, 7.12, NOW(), 'DEMO_SIMULATED'),
(4, 4, 'SEVERE', 12.50, 890, 10.08, NOW(), 'DEMO_SIMULATED'),
(5, 5, 'SEVERE', 14.00, 940, 9.85, NOW(), 'DEMO_SIMULATED'),
(6, 6, 'HEAVY', 28.00, 1120, 16.07, NOW(), 'DEMO_SIMULATED'),
(7, 7, 'LOW', 46.00, 240, 7.56, NOW(), 'DEMO_SIMULATED'),
(8, 8, 'MODERATE', 35.00, 510, 5.48, NOW(), 'DEMO_SIMULATED'),
(9, 9, 'HEAVY', 22.50, 760, 12.26, NOW(), 'DEMO_SIMULATED'),
(10, 10, 'HEAVY', 24.00, 850, 12.00, NOW(), 'DEMO_SIMULATED'),
(11, 11, 'LOW', 52.00, 210, 4.04, NOW(), 'DEMO_SIMULATED'),
(12, 12, 'LOW', 92.00, 310, 18.58, NOW(), 'DEMO_SIMULATED'),
(13, 13, 'LOW', 94.00, 290, 16.60, NOW(), 'DEMO_SIMULATED'),
(14, 14, 'MODERATE', 62.00, 480, 20.32, NOW(), 'DEMO_SIMULATED'),
(15, 15, 'MODERATE', 30.00, 430, 7.80, NOW(), 'DEMO_SIMULATED'),
(16, 16, 'HEAVY', 25.00, 710, 22.08, NOW(), 'DEMO_SIMULATED'),
(17, 17, 'MODERATE', 33.00, 490, 14.18, NOW(), 'DEMO_SIMULATED'),
(18, 18, 'SEVERE', 11.00, 620, 28.36, NOW(), 'DEMO_SIMULATED'),
(19, 19, 'HEAVY', 18.00, 540, 21.33, NOW(), 'DEMO_SIMULATED'),
(20, 20, 'MODERATE', 31.00, 600, 12.58, NOW(), 'DEMO_SIMULATED');

-- ----------------------------------------------------------------------------
-- 5. Insert Realistic Traffic Incidents
-- Critical, High, and Medium incidents affecting major Hyderabad bottlenecks
-- ----------------------------------------------------------------------------
INSERT INTO traffic_incidents (incident_id, road_id, incident_type, severity, description, status, reported_by_user_id, reported_at, impact_delay_minutes) VALUES
(1, 4, 'ACCIDENT', 'CRITICAL', 'Multi-vehicle collision near Panjagutta Central Mall flyover ascent. Right two lanes obstructed.', 'IN_PROGRESS', 1, DATE_SUB(NOW(), INTERVAL 45 MINUTE), 25),
(2, 5, 'CONSTRUCTION', 'HIGH', 'Hyderabad Metro Rail maintenance work between Panjagutta and Ameerpet Station. Single lane operational.', 'REPORTED', 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), 20),
(3, 10, 'WATERLOGGING', 'MEDIUM', 'Rain water accumulation near Bio-Diversity Park junction causing slow vehicular movement.', 'REPORTED', 2, DATE_SUB(NOW(), INTERVAL 1 HOUR), 15),
(4, 18, 'ROAD_CLOSURE', 'HIGH', 'Heritage street pedestrianization trial run near Charminar. Heavy diversions via Nayapul.', 'IN_PROGRESS', 1, DATE_SUB(NOW(), INTERVAL 3 HOUR), 30),
(5, 2, 'BREAKDOWN', 'LOW', 'RTC City Bus breakdown on Road No. 36 incline towards Jubilee Hills. Towing vehicle en route.', 'REPORTED', 2, DATE_SUB(NOW(), INTERVAL 20 MINUTE), 10);

-- ----------------------------------------------------------------------------
-- 6. Insert Precomputed Benchmark Routes (For Evaluation & Quick Selection)
-- ----------------------------------------------------------------------------
INSERT INTO routes (route_id, route_name, origin_location_id, destination_location_id, total_distance_km, estimated_duration_minutes, route_preference, route_score, algorithm_used, created_by_user_id) VALUES
(1, 'HITEC City to Airport via ORR (Fastest)', 1, 13, 33.30, 28.00, 'FASTEST', 94.50, 'DIJKSTRA', 1),
(2, 'HITEC City to Airport via Banjara-PVNR (Balanced)', 1, 13, 33.60, 48.00, 'BALANCED', 78.20, 'A_STAR', 1),
(3, 'HITEC City to Secunderabad via Jubilee Hills', 1, 10, 18.30, 52.00, 'BALANCED', 71.00, 'DIJKSTRA', 2),
(4, 'Kukatpally to Airport via ORR Bypass', 9, 13, 39.10, 36.00, 'FASTEST', 91.00, 'DIJKSTRA', 1);

-- ----------------------------------------------------------------------------
-- 7. Insert Route Segment Sequences (RouteRoads Bridge Table)
-- ----------------------------------------------------------------------------
-- Route 1: HITEC City -> Gachibowli -> Airport via ORR
INSERT INTO route_roads (route_id, road_id, sequence_number, segment_distance_km) VALUES
(1, 10, 1, 4.80),
(1, 12, 2, 28.50);

-- Route 2: HITEC City -> Mindspace -> Jubilee -> Banjara -> Mehdipatnam -> Airport PVNR
INSERT INTO route_roads (route_id, road_id, sequence_number, segment_distance_km) VALUES
(2, 1, 1, 2.50),
(2, 2, 2, 4.20),
(2, 3, 3, 3.80),
(2, 15, 4, 3.90),
(2, 14, 5, 21.00);

-- Route 3: HITEC City -> Mindspace -> Jubilee -> Banjara -> Panjagutta -> Begumpet -> Secunderabad
INSERT INTO route_roads (route_id, road_id, sequence_number, segment_distance_km) VALUES
(3, 1, 1, 2.50),
(3, 2, 2, 4.20),
(3, 3, 3, 3.80),
(3, 4, 4, 2.10),
(3, 8, 5, 3.20),
(3, 9, 6, 4.60);

-- Route 4: Kukatpally -> Hitec City -> Gachibowli -> Airport ORR
INSERT INTO route_roads (route_id, road_id, sequence_number, segment_distance_km) VALUES
(4, 7, 1, 5.80),
(4, 10, 2, 4.80),
(4, 12, 3, 28.50);

-- ----------------------------------------------------------------------------
-- 8. Insert Initial Audit Logs
-- ----------------------------------------------------------------------------
INSERT INTO audit_logs (log_id, user_id, action_type, entity_name, entity_id, change_summary, ip_address) VALUES
(1, 1, 'SYSTEM_INIT', 'DATABASE', 1, 'TrafficiQ database initialized with Hyderabad Metropolitan road network baseline', '127.0.0.1'),
(2, 1, 'CREATE_INCIDENT', 'TRAFFIC_INCIDENTS', 1, 'Reported critical multi-vehicle collision on Panjagutta Flyover', '127.0.0.1');
