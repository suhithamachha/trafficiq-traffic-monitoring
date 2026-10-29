-- ============================================================================
-- TRAFFICIQ: Comprehensive Sample Queries & DBMS Concept Demonstrations
-- DBMS Syllabus Implementation: Units I - V
-- File: database/sample_queries.sql
-- ============================================================================

USE trafficiq_db;

-- ============================================================================
-- PART 1: DDL, DML, DCL, and TCL Demonstrations (Unit III)
-- ============================================================================

-- DCL Demonstration: Granting Permissions to Application User
-- CREATE USER 'trafficiq_app'@'localhost' IDENTIFIED BY 'TrafficiQ@Secure2026';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON trafficiq_db.* TO 'trafficiq_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ============================================================================
-- PART 2: Relational Queries & Advanced JOINs (Unit III)
-- ============================================================================

-- Query 1: Multi-Table INNER JOIN
-- Retrieves roads with full location names, area codes, and distance
SELECT 
    r.road_code,
    r.road_name,
    s_loc.location_name AS origin,
    s_loc.area_code AS origin_pincode,
    e_loc.location_name AS destination,
    e_loc.area_code AS dest_pincode,
    r.distance_km,
    r.speed_limit_kmh
FROM roads r
INNER JOIN locations s_loc ON r.start_location_id = s_loc.location_id
INNER JOIN locations e_loc ON r.end_location_id = e_loc.location_id
ORDER BY r.distance_km DESC;

-- Query 2: LEFT OUTER JOIN with Aggregation & HAVING Clause
-- Find all locations that act as an origin for more than 1 road segment, with total outgoing distance
SELECT 
    l.location_name,
    l.landmark,
    COUNT(r.road_id) AS outgoing_routes_count,
    ROUND(SUM(r.distance_km), 2) AS total_outgoing_distance_km,
    ROUND(AVG(r.speed_limit_kmh), 1) AS avg_speed_limit
FROM locations l
LEFT JOIN roads r ON l.location_id = r.start_location_id
GROUP BY l.location_id, l.location_name, l.landmark
HAVING outgoing_routes_count >= 1
ORDER BY outgoing_routes_count DESC;

-- Query 3: Correlated Subquery
-- Retrieve roads where the current speed is lower than 50% of the road's posted speed limit (Severe Bottlenecks)
SELECT 
    r.road_name,
    r.speed_limit_kmh,
    ts.average_speed_kmh AS current_speed,
    ROUND((ts.average_speed_kmh / r.speed_limit_kmh) * 100, 1) AS efficiency_percentage
FROM roads r
JOIN traffic_snapshots ts ON r.road_id = ts.road_id
WHERE ts.snapshot_id = (
    SELECT MAX(sub_ts.snapshot_id)
    FROM traffic_snapshots sub_ts
    WHERE sub_ts.road_id = r.road_id
)
AND ts.average_speed_kmh < (r.speed_limit_kmh * 0.50);

-- Query 4: Subquery with IN and EXISTS Operator
-- Find all users who have reported at least one active traffic incident
SELECT u.user_id, u.name, u.email, u.role
FROM users u
WHERE EXISTS (
    SELECT 1 
    FROM traffic_incidents ti 
    WHERE ti.reported_by_user_id = u.user_id 
      AND ti.status IN ('REPORTED', 'IN_PROGRESS')
);

-- Query 5: Window Functions (ROW_NUMBER & DENSE_RANK)
-- Rank road segments by congestion delay within each road type
SELECT 
    r.road_type,
    r.road_name,
    ts.congestion_level,
    ts.estimated_travel_time_min,
    DENSE_RANK() OVER (
        PARTITION BY r.road_type 
        ORDER BY ts.estimated_travel_time_min DESC
    ) AS congestion_rank
FROM roads r
JOIN traffic_snapshots ts ON r.road_id = ts.road_id
WHERE ts.snapshot_id IN (
    SELECT MAX(snapshot_id) FROM traffic_snapshots GROUP BY road_id
);

-- Query 6: Set Operations (UNION)
-- List all critical incident locations combined with major traffic bottlenecks
SELECT 
    l.location_name, 
    'CRITICAL_INCIDENT' AS alert_category, 
    ti.description AS details
FROM traffic_incidents ti
JOIN roads r ON ti.road_id = r.road_id
JOIN locations l ON r.start_location_id = l.location_id
WHERE ti.severity = 'CRITICAL' AND ti.status <> 'RESOLVED'

UNION

SELECT 
    l.location_name, 
    'SEVERE_CONGESTION' AS alert_category, 
    CONCAT('Avg speed reduced to ', ts.average_speed_kmh, ' km/h') AS details
FROM traffic_snapshots ts
JOIN roads r ON ts.road_id = r.road_id
JOIN locations l ON r.start_location_id = l.location_id
WHERE ts.congestion_level = 'SEVERE';

-- ============================================================================
-- PART 3: Transaction Management & ACID Property Demonstration (Unit IV)
-- Demonstrating Atomic Multi-table Update, Savepoint, and Rollback
-- ============================================================================

-- Scenario: Admin reports an emergency bridge closure incident and simultaneously
-- marks the road's traffic snapshot as SEVERE within an atomic transaction.
START TRANSACTION;

-- Step 1: Insert Emergency Incident
INSERT INTO traffic_incidents (
    road_id, incident_type, severity, description, status, reported_by_user_id, impact_delay_minutes
) VALUES (
    4, 'ROAD_CLOSURE', 'CRITICAL', 'Emergency Flyover Structural Inspection - Temporary Closure', 'REPORTED', 1, 60
);

-- Establish Savepoint
SAVEPOINT after_incident_creation;

-- Step 2: Update Traffic Condition
INSERT INTO traffic_snapshots (
    road_id, congestion_level, average_speed_kmh, vehicle_count, estimated_travel_time_min, recorded_at, data_source
) VALUES (
    4, 'SEVERE', 5.00, 1200, 25.20, NOW(), 'DEMO_SIMULATED'
);

-- In case of any validation error or operator mistake:
-- ROLLBACK TO SAVEPOINT after_incident_creation;
-- Or to abort entirely:
-- ROLLBACK;

-- If successful, persist changes atomically:
COMMIT;

-- Verify the transaction result
SELECT * FROM v_road_current_traffic WHERE road_id = 4;
