-- ============================================================================
-- TRAFFICIQ: Relational Views for Analytical & Operational Dashboards
-- DBMS Syllabus Implementation: Unit II (Views & Logical Data Independence) & Unit III (Complex SQL)
-- File: database/views.sql
-- ============================================================================

USE trafficiq_db;

-- ----------------------------------------------------------------------------
-- View 1: v_road_current_traffic
-- Returns the most recent traffic status, live speed, estimated travel time,
-- and active incident summary for every road segment.
-- Demonstrates: Correlated subquery / Window Functions, Multiple Outer Joins
-- ----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_road_current_traffic;

CREATE VIEW v_road_current_traffic AS
SELECT 
    r.road_id,
    r.road_code,
    r.road_name,
    r.road_type,
    r.distance_km,
    r.speed_limit_kmh,
    r.lanes,
    r.is_toll,
    r.is_bidirectional,
    loc_s.location_id AS start_location_id,
    loc_s.location_name AS start_location_name,
    loc_s.latitude AS start_latitude,
    loc_s.longitude AS start_longitude,
    loc_e.location_id AS end_location_id,
    loc_e.location_name AS end_location_name,
    loc_e.latitude AS end_latitude,
    loc_e.longitude AS end_longitude,
    COALESCE(latest_snap.congestion_level, 'LOW') AS congestion_level,
    COALESCE(latest_snap.average_speed_kmh, r.speed_limit_kmh) AS current_speed_kmh,
    COALESCE(latest_snap.vehicle_count, 0) AS vehicle_count,
    COALESCE(latest_snap.estimated_travel_time_min, ROUND((r.distance_km / r.speed_limit_kmh) * 60, 2)) AS estimated_travel_time_min,
    latest_snap.recorded_at AS last_updated,
    latest_snap.data_source,
    COALESCE(inc.active_incident_count, 0) AS active_incidents,
    inc.highest_severity
FROM roads r
JOIN locations loc_s ON r.start_location_id = loc_s.location_id
JOIN locations loc_e ON r.end_location_id = loc_e.location_id
LEFT JOIN (
    SELECT s1.road_id, s1.congestion_level, s1.average_speed_kmh, s1.vehicle_count, 
           s1.estimated_travel_time_min, s1.recorded_at, s1.data_source
    FROM traffic_snapshots s1
    INNER JOIN (
        SELECT road_id, MAX(snapshot_id) AS max_snapshot_id
        FROM traffic_snapshots
        GROUP BY road_id
    ) s2 ON s1.snapshot_id = s2.max_snapshot_id
) latest_snap ON r.road_id = latest_snap.road_id
LEFT JOIN (
    SELECT 
        road_id,
        COUNT(incident_id) AS active_incident_count,
        MAX(CASE 
            WHEN severity = 'CRITICAL' THEN 'CRITICAL'
            WHEN severity = 'HIGH' THEN 'HIGH'
            WHEN severity = 'MEDIUM' THEN 'MEDIUM'
            ELSE 'LOW' END) AS highest_severity
    FROM traffic_incidents
    WHERE status IN ('REPORTED', 'IN_PROGRESS')
    GROUP BY road_id
) inc ON r.road_id = inc.road_id;

-- ----------------------------------------------------------------------------
-- View 2: v_active_incidents
-- Operational view of all unresolved traffic incidents with spatial details
-- ----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_active_incidents;

CREATE VIEW v_active_incidents AS
SELECT 
    i.incident_id,
    i.incident_type,
    i.severity,
    i.description,
    i.status,
    i.reported_at,
    i.impact_delay_minutes,
    r.road_id,
    r.road_code,
    r.road_name,
    r.road_type,
    l1.location_name AS start_location,
    l1.latitude AS start_lat,
    l1.longitude AS start_lng,
    l2.location_name AS end_location,
    l2.latitude AS end_lat,
    l2.longitude AS end_lng,
    ROUND((l1.latitude + l2.latitude) / 2, 7) AS incident_midpoint_lat,
    ROUND((l1.longitude + l2.longitude) / 2, 7) AS incident_midpoint_lng,
    u.name AS reported_by
FROM traffic_incidents i
JOIN roads r ON i.road_id = r.road_id
JOIN locations l1 ON r.start_location_id = l1.location_id
JOIN locations l2 ON r.end_location_id = l2.location_id
LEFT JOIN users u ON i.reported_by_user_id = u.user_id
WHERE i.status IN ('REPORTED', 'IN_PROGRESS');

-- ----------------------------------------------------------------------------
-- View 3: v_corridor_congestion_summary
-- Aggregated statistical metrics for executive and analytics reporting
-- Demonstrates: Aggregate functions (COUNT, AVG, SUM), Conditional Aggregation
-- ----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_corridor_congestion_summary;

CREATE VIEW v_corridor_congestion_summary AS
SELECT 
    r.road_type,
    COUNT(DISTINCT r.road_id) AS total_road_segments,
    ROUND(SUM(r.distance_km), 2) AS total_network_km,
    ROUND(AVG(COALESCE(latest_snap.average_speed_kmh, r.speed_limit_kmh)), 1) AS avg_speed_kmh,
    SUM(CASE WHEN latest_snap.congestion_level = 'LOW' THEN 1 ELSE 0 END) AS count_low_congestion,
    SUM(CASE WHEN latest_snap.congestion_level = 'MODERATE' THEN 1 ELSE 0 END) AS count_moderate_congestion,
    SUM(CASE WHEN latest_snap.congestion_level = 'HEAVY' THEN 1 ELSE 0 END) AS count_heavy_congestion,
    SUM(CASE WHEN latest_snap.congestion_level = 'SEVERE' THEN 1 ELSE 0 END) AS count_severe_congestion,
    ROUND(AVG(COALESCE(latest_snap.vehicle_count, 0)), 0) AS avg_vehicle_count
FROM roads r
LEFT JOIN (
    SELECT s1.road_id, s1.congestion_level, s1.average_speed_kmh, s1.vehicle_count
    FROM traffic_snapshots s1
    INNER JOIN (
        SELECT road_id, MAX(snapshot_id) AS max_snapshot_id
        FROM traffic_snapshots
        GROUP BY road_id
    ) s2 ON s1.snapshot_id = s2.max_snapshot_id
) latest_snap ON r.road_id = latest_snap.road_id
GROUP BY r.road_type;

-- ----------------------------------------------------------------------------
-- View 4: v_route_summaries
-- Route overview with sequenced roads string aggregated
-- ----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_route_summaries;

CREATE VIEW v_route_summaries AS
SELECT 
    rt.route_id,
    rt.route_name,
    o.location_name AS origin_name,
    d.location_name AS destination_name,
    rt.total_distance_km,
    rt.estimated_duration_minutes,
    rt.route_preference,
    rt.route_score,
    rt.algorithm_used,
    COUNT(rr.road_id) AS total_segments,
    GROUP_CONCAT(r.road_name ORDER BY rr.sequence_number SEPARATOR ' -> ') AS path_sequence,
    rt.created_at
FROM routes rt
JOIN locations o ON rt.origin_location_id = o.location_id
JOIN locations d ON rt.destination_location_id = d.location_id
LEFT JOIN route_roads rr ON rt.route_id = rr.route_id
LEFT JOIN roads r ON rr.road_id = r.road_id
GROUP BY rt.route_id, rt.route_name, o.location_name, d.location_name, 
         rt.total_distance_km, rt.estimated_duration_minutes, rt.route_preference, 
         rt.route_score, rt.algorithm_used, rt.created_at;
