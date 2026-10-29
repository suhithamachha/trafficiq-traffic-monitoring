-- ============================================================================
-- TRAFFICIQ: Indexing Strategy, B-Trees, and Query Optimization
-- DBMS Syllabus Implementation: Unit V (Indexing, B+ Trees, EXPLAIN Analysis)
-- File: database/indexes.sql
-- ============================================================================

USE trafficiq_db;

-- ----------------------------------------------------------------------------
-- 1. Secondary B-Tree Indexes on Foreign Keys and Frequent Search Columns
-- ----------------------------------------------------------------------------

-- A. Locations Spatial Coordinate Index
-- Accelerates bounding-box, radius search, and proximity queries
CREATE INDEX idx_locations_coords ON locations(latitude, longitude);

-- B. Road Endpoint Composite Index
-- Critical for graph edge lookups in Dijkstra / A* route planning
CREATE INDEX idx_roads_endpoints ON roads(start_location_id, end_location_id);
CREATE INDEX idx_roads_type ON roads(road_type);

-- C. Traffic Snapshots Compound Time-Series Index
-- Highly optimized for `WHERE road_id = ? ORDER BY recorded_at DESC`
CREATE INDEX idx_snapshots_road_time ON traffic_snapshots(road_id, recorded_at DESC);
CREATE INDEX idx_snapshots_congestion ON traffic_snapshots(congestion_level);

-- D. Traffic Incidents Operational Index
-- Supports real-time active incident feeds filtered by status and severity
CREATE INDEX idx_incidents_status_sev ON traffic_incidents(status, severity);
CREATE INDEX idx_incidents_road ON traffic_incidents(road_id);

-- E. Routes Optimization Index
CREATE INDEX idx_routes_endpoints ON routes(origin_location_id, destination_location_id);
CREATE INDEX idx_routeroads_seq ON route_roads(route_id, sequence_number);

-- F. Users Authentication Index
-- Accelerates Spring Security user lookup by email during login
-- (email already has UNIQUE constraint, which creates an index, but shown here for syllabus clarity)
CREATE INDEX idx_users_role ON users(role);

-- ----------------------------------------------------------------------------
-- 2. Query Performance & EXPLAIN Verification Examples (DBMS Unit V)
-- ----------------------------------------------------------------------------

-- Example 1: Verifying Index Usage on Latest Snapshot Lookup
-- Expected: Key `idx_snapshots_road_time` used with `ref` or `range`, avoiding full table scan.
EXPLAIN
SELECT * 
FROM traffic_snapshots 
WHERE road_id = 4 
ORDER BY recorded_at DESC 
LIMIT 1;

-- Example 2: Verifying Composite Index on Graph Search
-- Expected: Key `idx_roads_endpoints` enables direct B-Tree lookup for adjacent nodes.
EXPLAIN
SELECT road_id, road_name, distance_km, speed_limit_kmh 
FROM roads 
WHERE start_location_id = 1 AND end_location_id = 2;

-- Example 3: Verifying Active Incidents Query
-- Expected: `idx_incidents_status_sev` restricts scanned rows to only active statuses.
EXPLAIN
SELECT incident_id, description, severity, impact_delay_minutes 
FROM traffic_incidents 
WHERE status IN ('REPORTED', 'IN_PROGRESS') AND severity = 'CRITICAL';
