# TRAFFICIQ — Comprehensive DBMS Viva Preparation Guide (20 Questions & Answers)

This guide contains 20 academic viva examination questions with detailed, high-scoring technical answers mapped directly to the **TRAFFICIQ** implementation.

---

### Q1. What is the difference between Conceptual, Logical, and Physical schemas in the context of TrafficiQ?
**Answer:**
- **Conceptual Schema**: High-level abstract model representing entities and business relationships without DBMS-specific syntax. In TrafficiQ, this is our Entity-Relationship (ER) model defining entities (`Users`, `Roads`, `Locations`, `Incidents`) and their cardinalities.
- **Logical Schema**: Detailed relational mapping adhering to relational constraints, primary keys, foreign keys, and normalization rules (1NF–3NF/BCNF). Implemented in `schema.sql`.
- **Physical Schema**: Low-level storage structures, InnoDB table spaces, page sizes, clustered B-Tree disk layout, and secondary indexes defined in `indexes.sql`.

---

### Q2. Why is `RouteRoads` modeled as a bridge table with a composite primary key?
**Answer:**
A route consists of multiple sequenced road segments, and a single road segment can be part of many different routes. This represents a **Many-to-Many (M:N) relationship** between `Routes` and `Roads`.
Relational databases cannot store repeating groups or arrays directly without violating **First Normal Form (1NF)**. Therefore, the relation is decomposed into the bridge table `RouteRoads` with a composite primary key `{route_id, road_id, sequence_number}`. This structure guarantees that every road segment in a route has a strictly unique sequence number and avoids partial dependencies (satisfying 2NF and 3NF).

---

### Q3. Explain the difference between 3NF and BCNF, and prove TrafficiQ satisfies BCNF.
**Answer:**
- **Third Normal Form (3NF)**: Requires that the relation is in 2NF and no non-prime attribute is transitively dependent on any candidate key. Formally, for every non-trivial functional dependency $X \to Y$, either $X$ is a superkey OR $Y$ is a prime attribute.
- **Boyce-Codd Normal Form (BCNF)**: Stricter than 3NF. For every non-trivial functional dependency $X \to Y$, $X$ **must** be a superkey (eliminates candidate keys overlapping).
- **Proof in TrafficiQ**: In the `Roads` relation, the candidate keys are `road_id` and `road_code`. The functional dependencies are:
  - $road\_id \to \{road\_name, road\_code, start\_loc, end\_loc, distance, speed\_limit, \dots\}$
  - $road\_code \to \{road\_id, road\_name, start\_loc, end\_loc, distance, speed\_limit, \dots\}$
  Both determinants ($road\_id$ and $road\_code$) are superkeys. There are no dependencies where the determinant is not a superkey. Hence, `Roads` is in BCNF.

---

### Q4. How do Database Triggers maintain consistency in TrafficiQ? Give a concrete example.
**Answer:**
Triggers are Event-Condition-Action (ECA) rules executed automatically by the DBMS engine in response to DML operations (`INSERT`, `UPDATE`, `DELETE`).
In TrafficiQ, `trg_after_incident_insert` fires whenever an incident is reported. If an administrator inserts a `CRITICAL` or `HIGH` severity accident, the trigger automatically computes degraded travel speeds and generates a corresponding `SEVERE` or `HEAVY` row in `traffic_snapshots` and an entry in `audit_logs`. This ensures business rule enforcement happens directly inside the database kernel, preventing stale or inconsistent states even if accessed outside the application server.

---

### Q5. What is the difference between a Clustered Index and a Secondary Index in MySQL InnoDB?
**Answer:**
- **Clustered Index**: The actual physical table data rows are stored in the leaf pages of the index's B+ Tree ordered by the Primary Key. InnoDB automatically creates a clustered index on the primary key (e.g. `road_id` in `Roads`). There can be only **one** clustered index per table.
- **Secondary Index**: An auxiliary B+ Tree where the leaf nodes do not contain full data rows, but rather the index column values and the primary key pointer. In TrafficiQ, `idx_roads_endpoints (start_location_id, end_location_id)` is a secondary index. A query looking up roads by endpoints first traverses the secondary B+ Tree, retrieves `road_id`, and then performs a bookmark lookup into the clustered index.

---

### Q6. Why did we create a compound index `(road_id, recorded_at DESC)` on `traffic_snapshots`?
**Answer:**
In real-time monitoring systems, queries frequently fetch the **latest snapshot** for a given road:
`SELECT * FROM traffic_snapshots WHERE road_id = ? ORDER BY recorded_at DESC LIMIT 1;`
Without this compound index, MySQL would first filter by `road_id` and then perform an expensive in-memory sort (`Using filesort`).
The compound index `(road_id, recorded_at DESC)` allows the database to locate the road's records in the B+ Tree and immediately read the top leaf node in pre-sorted descending order without any filesort, executing in $O(\log N)$ time.

---

### Q7. Explain how the SQL `EXPLAIN` command works and what `type: ALL` versus `type: ref` indicates.
**Answer:**
`EXPLAIN` displays the query execution plan generated by the DBMS Query Optimizer:
- `type: ALL`: Indicates a **Full Table Scan**. The database engine must scan every single row on disk from start to finish. High cost for large datasets.
- `type: ref` or `type: eq_ref`: Indicates that an index is utilized to look up matching rows. `eq_ref` is the most efficient, used when the index is unique or a primary key. In TrafficiQ, `indexes.sql` demonstrates how adding `idx_incidents_status_sev` converts queries from `type: ALL` to `type: ref`.

---

### Q8. How are ACID properties guaranteed during incident creation in TrafficiQ?
**Answer:**
When an incident is reported:
1. **Atomicity**: Enclosed in Spring's `@Transactional` boundary. If inserting into `traffic_incidents` succeeds but the `audit_logs` write fails, the entire transaction is rolled back via MySQL undo logs.
2. **Consistency**: Invariants such as non-negative distance and valid foreign keys are enforced by database schema constraints.
3. **Isolation**: InnoDB uses `REPEATABLE READ` with row-level locks and MVCC so concurrent transactions do not see partial or uncommitted incident modifications.
4. **Durability**: Upon `COMMIT`, transaction logs are flushed to disk (redo log write-ahead logging), guaranteeing persistence across system failure.

---

### Q9. What is Multi-Version Concurrency Control (MVCC) in MySQL InnoDB?
**Answer:**
MVCC allows concurrent read and write operations without blocking each other. Each modified row retains version metadata (system transaction IDs `DB_TRX_ID` and roll pointers `DB_ROLL_PTR`).
When a read query runs, it reads from a snapshot corresponding to when the read transaction started. As a result, **reads never block writes, and writes never block reads**.

---

### Q10. What is Logical Data Independence, and how do Views provide it in TrafficiQ?
**Answer:**
Logical data independence is the ability to alter the logical schema (e.g. splitting or merging tables) without altering external user schemas or application queries.
In TrafficiQ, the view `v_road_current_traffic` joins `Roads`, `Locations`, `TrafficSnapshots`, and `TrafficIncidents`. If internal snapshot storage is refactored (e.g. partitioned by month), only the view definition is updated. Client applications query `v_road_current_traffic` without requiring any backend code changes.

---

### Q11. Why did we use Dijkstra’s algorithm in Python rather than recursive SQL Common Table Expressions (CTEs)?
**Answer:**
While modern SQL supports recursive CTEs (`WITH RECURSIVE`), graph pathfinding with multi-criteria dynamic edge weights (factoring distance, congestion factor, speed limits, and incident penalty functions) requires complex arithmetic, min-priority queues, and alternative path discovery.
Executing this in Python's microservice layer with `heapq` decouples heavy graph computation from the database engine, adhering to separation of concerns and preventing database CPU saturation.

---

### Q12. Explain the difference between `ON DELETE CASCADE` and `ON DELETE RESTRICT`.
**Answer:**
- `ON DELETE CASCADE`: When a parent row is deleted, all matching child rows in foreign key referencing tables are automatically deleted by the DBMS. Used in TrafficiQ between `Roads` and `traffic_snapshots` (if a road is decommissioned, its historical snapshots are deleted).
- `ON DELETE RESTRICT` (or `NO ACTION`): Prevents deletion of the parent row if any child record references it. Used between `Locations` and `Roads` (`start_location_id`), ensuring a critical Hyderabad junction cannot be accidentally deleted if existing road corridors are mapped to it.

---

### Q13. How are user passwords secured, and why is MD5 or SHA-256 unsuitable for production?
**Answer:**
TrafficiQ uses **BCrypt** with an adaptive work factor (salt rounds = 10).
Algorithms like MD5 and plain SHA-256 are cryptographic hash functions designed to be extremely fast. Because of high GPU calculation speeds, they are vulnerable to precomputed Rainbow Table attacks and brute-force cracking.
BCrypt incorporates a cryptographically secure random 128-bit salt and an exponential cost parameter (Key Derivation Function) that makes password cracking computationally prohibitive.

---

### Q14. What is a Correlated Subquery? Where is it implemented in TrafficiQ?
**Answer:**
A correlated subquery is a nested inner query that evaluates once for every candidate row processed by the outer query because it references a column from the outer query.
In `sample_queries.sql`, Query 3 retrieves road segments where current speed is below 50% of the speed limit by evaluating:
`WHERE ts.snapshot_id = (SELECT MAX(sub_ts.snapshot_id) FROM traffic_snapshots sub_ts WHERE sub_ts.road_id = r.road_id)`
The inner query depends on `r.road_id` from the outer table.

---

### Q15. What are Window Functions, and how do they differ from `GROUP BY`?
**Answer:**
`GROUP BY` collapses multiple matching rows into a single aggregated summary row.
**Window Functions** (e.g. `ROW_NUMBER()`, `DENSE_RANK()`, `AVG() OVER()`) perform calculations across a partition of rows while **retaining individual row identities**.
In TrafficiQ, `DENSE_RANK() OVER (PARTITION BY road_type ORDER BY travel_time DESC)` ranks congested roads within each category without grouping them into a single row.

---

### Q16. What is a Deadlock in DBMS, and how can it be prevented?
**Answer:**
A deadlock occurs in concurrent multi-transaction environments when two or more transactions hold locks that the other requires in a cyclic dependency (e.g., Transaction A holds Lock 1 and requests Lock 2; Transaction B holds Lock 2 and requests Lock 1).
Prevention strategies:
1. Access resources in a strict predefined chronological order (e.g. always lock `roads` before `traffic_snapshots`).
2. Keep transactions short and focused.
3. Use lock timeout parameters (`innodb_lock_wait_timeout`). InnoDB automatically detects cycles via its wait-for graph and rolls back the transaction with the fewest row changes.

---

### Q17. Compare B-Tree Indexes with Hash Indexes.
**Answer:**
- **B+ Tree Index**: Tree structure maintaining sorted order. Supports range queries (`BETWEEN`, `<`, `>`), sorting (`ORDER BY`), and prefix matching (`LIKE 'HYD%'`). Lookups take $O(\log N)$ time.
- **Hash Index**: Hash table mapping keys to row addresses using a hash function. Extremely fast for exact equality lookups ($O(1)$), but **cannot** support range queries, partial prefix searches, or sorting.
TrafficiQ uses B+ Trees because traffic queries rely heavily on range filters (`speed_limit BETWEEN 20 AND 80`) and sorting (`ORDER BY recorded_at DESC`).

---

### Q18. What is the role of `SAVEPOINT` in transaction management?
**Answer:**
A `SAVEPOINT` creates an intermediate checkpoint within an open transaction. If a subsequent non-fatal error occurs, the application can issue `ROLLBACK TO SAVEPOINT savepoint_name` to undo actions up to that checkpoint without terminating or rolling back the entire transaction.

---

### Q19. How does the 3-tier architecture enhance security and data integrity in TrafficiQ?
**Answer:**
Client browsers never connect directly to MySQL port 3306.
All database access is routed through the **Java Spring Boot Application Tier**, which enforces:
1. Authentication via signed JWT tokens.
2. Parameterized queries / Hibernate ORM preventing SQL Injection.
3. Role-Based Access Control ensuring only `ROLE_ADMIN` can execute DDL/DML mutations.

---

### Q20. If live traffic telemetry scaled to 100,000 snapshots per minute, how would you re-architect the DBMS?
**Answer:**
1. **Range Partitioning**: Partition `traffic_snapshots` by month/day (`PARTITION BY RANGE (UNIX_TIMESTAMP(recorded_at))`) to drop old data instantly without table locks.
2. **Read-Write Splitting**: Route writes to a primary MySQL instance and distribute analytical dashboard reads across read-replicas.
3. **In-Memory Caching**: Cache latest road status in Redis to avoid hitting disk for frequent polyline polling.
4. **Time-Series Engine**: Migrate raw historical snapshots to a specialized time-series store (TimescaleDB or BigQuery) while keeping roads/metadata in MySQL.
