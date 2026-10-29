-- ============================================================================
-- TRAFFICIQ: Real-Time Traffic Monitoring & Intelligent Route Database
-- DBMS Syllabus Implementation: Units I - V
-- Target DBMS: MySQL 8.0+
-- File: database/schema.sql
-- ============================================================================

DROP DATABASE IF EXISTS trafficiq_db;
CREATE DATABASE trafficiq_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE trafficiq_db;

-- ----------------------------------------------------------------------------
-- 1. Table: users
-- Core entity for system users with Role-Based Access Control (RBAC)
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    phone VARCHAR(20) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_email CHECK (email LIKE '%_@__%.__%')
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 2. Table: locations
-- Spatial reference points across Hyderabad metropolitan region
-- ----------------------------------------------------------------------------
CREATE TABLE locations (
    location_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_name VARCHAR(150) NOT NULL UNIQUE,
    area_code VARCHAR(50) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    landmark VARCHAR(200) NULL,
    city VARCHAR(50) NOT NULL DEFAULT 'Hyderabad',
    CONSTRAINT chk_coordinates CHECK (
        latitude BETWEEN -90.0000000 AND 90.0000000 AND
        longitude BETWEEN -180.0000000 AND 180.0000000
    )
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 3. Table: roads
-- Road network segments with topological connections (Start and End Locations)
-- ----------------------------------------------------------------------------
CREATE TABLE roads (
    road_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    road_name VARCHAR(150) NOT NULL,
    road_code VARCHAR(50) NOT NULL UNIQUE,
    start_location_id BIGINT NOT NULL,
    end_location_id BIGINT NOT NULL,
    road_type ENUM('EXPRESSWAY', 'ARTERIAL', 'SUB_ARTERIAL', 'COLLECTOR') NOT NULL DEFAULT 'ARTERIAL',
    distance_km DECIMAL(6, 2) NOT NULL,
    speed_limit_kmh INT NOT NULL DEFAULT 50,
    lanes INT NOT NULL DEFAULT 4,
    is_toll BOOLEAN NOT NULL DEFAULT FALSE,
    is_bidirectional BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_roads_start_loc FOREIGN KEY (start_location_id) 
        REFERENCES locations(location_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_roads_end_loc FOREIGN KEY (end_location_id) 
        REFERENCES locations(location_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_distinct_endpoints CHECK (start_location_id <> end_location_id),
    CONSTRAINT chk_road_distance CHECK (distance_km > 0.0),
    CONSTRAINT chk_speed_limit CHECK (speed_limit_kmh BETWEEN 20 AND 140)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 4. Table: traffic_snapshots
-- Time-series traffic condition readings for road segments
-- ----------------------------------------------------------------------------
CREATE TABLE traffic_snapshots (
    snapshot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    road_id BIGINT NOT NULL,
    congestion_level ENUM('LOW', 'MODERATE', 'HEAVY', 'SEVERE') NOT NULL DEFAULT 'LOW',
    average_speed_kmh DECIMAL(5, 2) NOT NULL,
    vehicle_count INT NOT NULL DEFAULT 0,
    estimated_travel_time_min DECIMAL(6, 2) NOT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_source ENUM('DEMO_SIMULATED', 'SENSOR_VIRTUAL', 'POLICE_FEED') NOT NULL DEFAULT 'DEMO_SIMULATED',
    CONSTRAINT fk_snapshots_road FOREIGN KEY (road_id) 
        REFERENCES roads(road_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_avg_speed CHECK (average_speed_kmh >= 0.0),
    CONSTRAINT chk_travel_time CHECK (estimated_travel_time_min >= 0.0)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 5. Table: traffic_incidents
-- Real-time traffic alerts, disruptions, road closures, and accidents
-- ----------------------------------------------------------------------------
CREATE TABLE traffic_incidents (
    incident_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    road_id BIGINT NOT NULL,
    incident_type ENUM('ACCIDENT', 'ROAD_CLOSURE', 'CONSTRUCTION', 'WATERLOGGING', 'BREAKDOWN', 'PROTEST') NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    description TEXT NOT NULL,
    status ENUM('REPORTED', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'REPORTED',
    reported_by_user_id BIGINT NULL,
    reported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    impact_delay_minutes INT NOT NULL DEFAULT 15,
    CONSTRAINT fk_incidents_road FOREIGN KEY (road_id) 
        REFERENCES roads(road_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_incidents_user FOREIGN KEY (reported_by_user_id) 
        REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_impact_delay CHECK (impact_delay_minutes >= 0)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 6. Table: routes
-- Optimized and recommended travel paths computed by Dijkstra/A* engine
-- ----------------------------------------------------------------------------
CREATE TABLE routes (
    route_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_name VARCHAR(150) NOT NULL,
    origin_location_id BIGINT NOT NULL,
    destination_location_id BIGINT NOT NULL,
    total_distance_km DECIMAL(6, 2) NOT NULL,
    estimated_duration_minutes DECIMAL(6, 2) NOT NULL,
    route_preference ENUM('FASTEST', 'SHORTEST', 'BALANCED') NOT NULL DEFAULT 'BALANCED',
    route_score DECIMAL(5, 2) NOT NULL DEFAULT 85.00,
    algorithm_used ENUM('DIJKSTRA', 'A_STAR') NOT NULL DEFAULT 'DIJKSTRA',
    created_by_user_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_routes_origin FOREIGN KEY (origin_location_id) 
        REFERENCES locations(location_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_routes_destination FOREIGN KEY (destination_location_id) 
        REFERENCES locations(location_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_routes_user FOREIGN KEY (created_by_user_id) 
        REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_distinct_route_nodes CHECK (origin_location_id <> destination_location_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 7. Table: route_roads
-- Bridge table resolving M:N relationship between Routes and Road Segments
-- ----------------------------------------------------------------------------
CREATE TABLE route_roads (
    route_id BIGINT NOT NULL,
    road_id BIGINT NOT NULL,
    sequence_number INT NOT NULL,
    segment_distance_km DECIMAL(5, 2) NOT NULL,
    PRIMARY KEY (route_id, road_id, sequence_number),
    CONSTRAINT fk_routeroads_route FOREIGN KEY (route_id) 
        REFERENCES routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_routeroads_road FOREIGN KEY (road_id) 
        REFERENCES roads(road_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_seq_positive CHECK (sequence_number > 0)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 8. Table: audit_logs
-- Administrative actions, incident status modifications, and security audits
-- ----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action_type VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    change_summary TEXT NOT NULL,
    ip_address VARCHAR(45) NULL DEFAULT '127.0.0.1',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;
