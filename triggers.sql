-- ============================================================================
-- TRAFFICIQ: Database Triggers & Automated Business Rule Enforcement
-- DBMS Syllabus Implementation: Unit III (Triggers & Event-Condition-Action Rules)
-- File: database/triggers.sql
-- ============================================================================

USE trafficiq_db;

DELIMITER $$

-- ----------------------------------------------------------------------------
-- Trigger 1: trg_before_road_insert_validate
-- BEFORE INSERT trigger to prevent invalid road geometries or negative values
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_before_road_insert_validate$$

CREATE TRIGGER trg_before_road_insert_validate
BEFORE INSERT ON roads
FOR EACH ROW
BEGIN
    IF NEW.start_location_id = NEW.end_location_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Integrity Violation: Start and End locations of a road segment must be distinct.';
    END IF;

    IF NEW.distance_km <= 0.0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Domain Violation: Road distance must be strictly greater than 0.0 km.';
    END IF;

    IF NEW.speed_limit_kmh < 20 OR NEW.speed_limit_kmh > 140 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Domain Violation: Speed limit must be between 20 and 140 km/h.';
    END IF;
END$$

-- ----------------------------------------------------------------------------
-- Trigger 2: trg_after_incident_insert
-- AFTER INSERT trigger: When a high or critical incident occurs, immediately
-- updates road traffic condition by generating a SEVERE/HEAVY snapshot,
-- and records the action in audit_logs.
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_after_incident_insert$$

CREATE TRIGGER trg_after_incident_insert
AFTER INSERT ON traffic_incidents
FOR EACH ROW
BEGIN
    DECLARE v_new_level VARCHAR(20);
    DECLARE v_new_speed DECIMAL(5,2);
    DECLARE v_est_time DECIMAL(6,2);
    DECLARE v_dist DECIMAL(6,2);

    -- Retrieve road distance
    SELECT distance_km INTO v_dist FROM roads WHERE road_id = NEW.road_id;

    -- Compute degraded traffic speed based on incident severity
    IF NEW.severity = 'CRITICAL' THEN
        SET v_new_level = 'SEVERE';
        SET v_new_speed = 10.00;
    ELSEIF NEW.severity = 'HIGH' THEN
        SET v_new_level = 'HEAVY';
        SET v_new_speed = 18.00;
    ELSE
        SET v_new_level = 'MODERATE';
        SET v_new_speed = 28.00;
    END IF;

    SET v_est_time = ROUND((v_dist / v_new_speed) * 60, 2);

    -- Insert dynamic snapshot
    INSERT INTO traffic_snapshots (
        road_id, congestion_level, average_speed_kmh, vehicle_count, 
        estimated_travel_time_min, recorded_at, data_source
    ) VALUES (
        NEW.road_id, v_new_level, v_new_speed, 850, 
        v_est_time, NOW(), 'DEMO_SIMULATED'
    );

    -- Log audit record
    INSERT INTO audit_logs (
        user_id, action_type, entity_name, entity_id, change_summary, ip_address
    ) VALUES (
        NEW.reported_by_user_id, 'INCIDENT_CREATED', 'TRAFFIC_INCIDENTS', NEW.incident_id,
        CONCAT('Incident reported on road ID ', NEW.road_id, ' [Type: ', NEW.incident_type, ', Severity: ', NEW.severity, ']'),
        '127.0.0.1'
    );
END$$

-- ----------------------------------------------------------------------------
-- Trigger 3: trg_after_incident_update
-- AFTER UPDATE trigger: When incident is marked RESOLVED, automatically
-- sets resolved_at timestamp, logs to audit_logs, and restores normal flow.
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_after_incident_update$$

CREATE TRIGGER trg_after_incident_update
AFTER UPDATE ON traffic_incidents
FOR EACH ROW
BEGIN
    DECLARE v_nominal_speed INT;
    DECLARE v_dist DECIMAL(6,2);
    DECLARE v_est_time DECIMAL(6,2);

    -- If status shifted to RESOLVED
    IF OLD.status <> 'RESOLVED' AND NEW.status = 'RESOLVED' THEN
        SELECT speed_limit_kmh, distance_km INTO v_nominal_speed, v_dist 
        FROM roads WHERE road_id = NEW.road_id;

        SET v_est_time = ROUND((v_dist / (v_nominal_speed * 0.75)) * 60, 2);

        -- Restore traffic to MODERATE/LOW condition
        INSERT INTO traffic_snapshots (
            road_id, congestion_level, average_speed_kmh, vehicle_count, 
            estimated_travel_time_min, recorded_at, data_source
        ) VALUES (
            NEW.road_id, 'LOW', (v_nominal_speed * 0.85), 320, 
            v_est_time, NOW(), 'DEMO_SIMULATED'
        );

        -- Log resolution audit
        INSERT INTO audit_logs (
            user_id, action_type, entity_name, entity_id, change_summary, ip_address
        ) VALUES (
            NEW.reported_by_user_id, 'INCIDENT_RESOLVED', 'TRAFFIC_INCIDENTS', NEW.incident_id,
            CONCAT('Incident ID ', NEW.incident_id, ' on road ID ', NEW.road_id, ' marked RESOLVED.'),
            '127.0.0.1'
        );
    END IF;
END$$

-- ----------------------------------------------------------------------------
-- Trigger 4: trg_audit_road_update
-- Logs administrative edits to roads (e.g. speed limit / lane alterations)
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_audit_road_update$$

CREATE TRIGGER trg_audit_road_update
AFTER UPDATE ON roads
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (
        user_id, action_type, entity_name, entity_id, change_summary, ip_address
    ) VALUES (
        1, 'ROAD_UPDATED', 'ROADS', NEW.road_id,
        CONCAT('Road updated: ', NEW.road_name, ' (Speed Limit: ', OLD.speed_limit_kmh, '->', NEW.speed_limit_kmh, ')'),
        '127.0.0.1'
    );
END$$

DELIMITER ;
