# TRAFFICIQ — DBMS Syllabus Mapping (Units I – V)

This document provides a comprehensive mapping between the standard **B.Tech Computer Science and Engineering (CSE) Database Management Systems (DBMS)** syllabus and the concrete architectural implementations, tables, scripts, and queries in **TRAFFICIQ**.

---

## Comprehensive Syllabus Mapping Matrix

| Syllabus Unit | Core Academic Topics | Implementation in TRAFFICIQ | Concrete Project Artifacts |
| :--- | :--- | :--- | :--- |
| **Unit I: Introduction & Database Design** | - Database System Concepts & Architecture<br>- Data Models (Relational, Network, Hierarchical)<br>- Schemas & Instances<br>- 3-Schema Architecture & Data Independence<br>- Entity-Relationship (ER) Modeling<br>- Entity Sets, Attributes, Keys, Relationships<br>- Cardinality Constraints & Participation | - 3-Tier Architecture (Client Web UI &rarr; Java Spring Boot &rarr; MySQL 8.x)<br>- Logical data independence through SQL Views (`v_road_current_traffic`, `v_active_incidents`)<br>- Comprehensive ER Diagram with 8 Entities<br>- 1:N relationships (`Users` &rarr; `Incidents`, `Roads` &rarr; `Snapshots`)<br>- M:N relationship resolved via bridge table (`Routes` &harr; `Roads` via `RouteRoads`)<br>- Total and partial participation constraints | - [architecture.md](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/docs/architecture.md)<br>- [er-diagram.md](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/docs/er-diagram.md)<br>- [schema.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/schema.sql) |
| **Unit II: Relational Model & Normalization** | - Relational Model Concepts<br>- Integrity Constraints (Domain, Entity, Referential)<br>- Relational Algebra & Calculus<br>- Functional Dependencies (FDs)<br>- Normal Forms: 1NF, 2NF, 3NF, BCNF<br>- Decomposition & Lossless Join | - Foreign key cascade rules (`ON DELETE CASCADE`, `ON DELETE RESTRICT`)<br>- Domain constraints: `CHECK (distance_km > 0.0)`, `CHECK (speed_limit_kmh BETWEEN 20 AND 140)`, `CHECK (start_location_id <> end_location_id)`<br>- Formal Proofs for 1NF, 2NF, 3NF, and BCNF<br>- Relational Algebra formulations ($\sigma, \Pi, \bowtie$) | - [schema.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/schema.sql)<br>- [er-diagram.md](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/docs/er-diagram.md#3-normalization-verification-dbms-syllabus-unit-ii) |
| **Unit III: Structured Query Language (SQL)** | - DDL, DML, DCL, TCL Statements<br>- Complex JOINs (INNER, LEFT OUTER, FULL)<br>- Aggregate Functions (`COUNT`, `AVG`, `SUM`)<br>- `GROUP BY` and `HAVING` Clauses<br>- Nested & Correlated Subqueries<br>- Set Operations (`UNION`, `EXISTS`, `IN`)<br>- Views (Updatable vs Read-only)<br>- Database Triggers (`BEFORE`, `AFTER`) | - Multi-table queries joining 4+ entities<br>- Correlated subqueries detecting roads with >50% speed degradation<br>- Window functions (`DENSE_RANK()`, `ROW_NUMBER()` over road types)<br>- Views: `v_road_current_traffic`, `v_corridor_congestion_summary`<br>- Triggers: `trg_after_incident_insert`, `trg_after_incident_update`, `trg_before_road_insert_validate` | - [schema.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/schema.sql)<br>- [views.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/views.sql)<br>- [triggers.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/triggers.sql)<br>- [sample_queries.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/sample_queries.sql) |
| **Unit IV: Transaction Management & Concurrency Control** | - Transaction Concept & ACID Properties<br>- Transaction States & Lifecycle<br>- Concurrent Executions & Anomalies (Dirty Read, Lost Update)<br>- Serializability (Conflict & View)<br>- Lock-Based Concurrency Control (2PL)<br>- Deadlock Detection & Prevention<br>- Recovery Concepts (WAL, Checkpoints) | - Spring `@Transactional` declarative boundary management on `RoadService` and `IncidentService`<br>- ACID multi-table atomic modification: updating incident + creating snapshot + appending audit log<br>- `START TRANSACTION`, `SAVEPOINT`, `ROLLBACK`, `COMMIT` scripts<br>- Isolation level configuration in Spring Data JPA | - [IncidentService.java](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/java-backend/src/main/java/com/trafficiq/service/IncidentService.java)<br>- [sample_queries.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/sample_queries.sql#part-3-transaction-management--acid-property-demonstration-unit-iv) |
| **Unit V: Storage, Indexing & Query Optimization** | - File Organization & Storage Structures<br>- Indexing: Primary, Clustered, Secondary<br>- B-Trees and B+ Trees Architecture<br>- Hash-based vs Tree-based Indexing<br>- Query Processing Steps & Cost Estimation<br>- Query Execution Plans (`EXPLAIN`)<br>- Index Optimization Techniques | - Clustered primary B-Tree indexes on surrogate keys<br>- Secondary composite B-Tree indexes: `idx_roads_endpoints(start_location_id, end_location_id)`<br>- Compound time-series index: `idx_snapshots_road_time(road_id, recorded_at DESC)`<br>- Spatial coordinate index on locations<br>- Concrete `EXPLAIN` query execution plans evaluating cost reduction | - [indexes.sql](file:///C:/Users/Suhitha/.gemini/antigravity/scratch/trafficiq/database/indexes.sql) |

---

## Detailed Academic Explanations

### 1. Data Independence (Unit I)
- **Physical Data Independence**: The underlying physical file layout, MySQL InnoDB table partitions, and B-Tree index structures can be re-architected or moved without modifying the Java JPA entity mappings or API endpoints.
- **Logical Data Independence**: When complex schema alterations occur, database views like `v_road_current_traffic` decouple frontend and analytical queries from raw table alterations.

### 2. Normalization Analysis (Unit II)
- Every relation adheres to **BCNF (Boyce-Codd Normal Form)**:
  - In `Roads`, the candidate keys are `road_id` (PK) and `road_code` (Unique). All functional dependencies ($road\_id \to \{attributes\}$ and $road\_code \to \{attributes\}$) have a determinant $X$ that is a superkey.
  - No partial dependency exists in the bridge table `RouteRoads` because `segment_distance_km` is fully functionally dependent on the composite key `{route_id, road_id, sequence_number}`.

### 3. ACID Properties in TrafficiQ (Unit IV)
- **Atomicity**: In `IncidentService.createIncident()`, inserting the incident, creating the degraded traffic snapshot, and recording the audit entry are enclosed in an atomic transaction. If any database write fails, the entire transaction is rolled back.
- **Consistency**: Invariant check constraints (`chk_distinct_endpoints`, `chk_speed_limit`) prevent corrupt or physically impossible states from entering the road network.
- **Isolation**: Handled via InnoDB's default `REPEATABLE READ` snapshot isolation utilizing Multi-Version Concurrency Control (MVCC) and next-key row locks, preventing dirty and non-repeatable reads.
- **Durability**: Upon `COMMIT`, modifications are flushed to the InnoDB Write-Ahead Log (redo log), guaranteeing persistence across system restarts or server crashes.

### 4. Index Selection & Query Optimization (Unit V)
- In urban road graphs, finding adjacent nodes for Dijkstra graph expansion requires constant lookups:
  `SELECT * FROM roads WHERE start_location_id = ? AND end_location_id = ?`
  Without an index, MySQL executes a full table scan ($O(N)$).
  By creating the composite B-Tree index `idx_roads_endpoints`, lookup time drops to $O(\log B)$ B-Tree tree traversals.
