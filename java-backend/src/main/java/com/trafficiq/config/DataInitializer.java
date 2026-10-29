package com.trafficiq.config;

import com.trafficiq.entity.*;
import com.trafficiq.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UserRepository userRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private RoadRepository roadRepository;
    @Autowired private TrafficSnapshotRepository snapshotRepository;
    @Autowired private IncidentRepository incidentRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedLocationsAndRoads();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@trafficiq.com")) {
            User admin = new User(
                    "System Administrator",
                    "admin@trafficiq.com",
                    passwordEncoder.encode("Admin@123"),
                    User.Role.ADMIN
            );
            admin.setPhone("+91 98480 22338");
            userRepository.save(admin);
            log.info("Initialized default Admin: admin@trafficiq.com / Admin@123");
        }

        if (!userRepository.existsByEmail("user@trafficiq.com")) {
            User user = new User(
                    "Demo Commuter",
                    "user@trafficiq.com",
                    passwordEncoder.encode("User@123"),
                    User.Role.USER
            );
            user.setPhone("+91 94400 11223");
            userRepository.save(user);
            log.info("Initialized default User: user@trafficiq.com / User@123");
        }
    }

    private void seedLocationsAndRoads() {
        if (locationRepository.count() == 0) {
            log.info("Seeding baseline Hyderabad spatial locations...");
            Location l1 = locationRepository.save(new Location("HITEC City (Cyber Towers)", "500081", new BigDecimal("17.4504000"), new BigDecimal("78.3808000"), "Cyber Towers"));
            Location l2 = locationRepository.save(new Location("Madhapur (Mindspace)", "500081", new BigDecimal("17.4399000"), new BigDecimal("78.3815000"), "Mindspace IT Park"));
            Location l3 = locationRepository.save(new Location("Gachibowli (Bio-Diversity)", "500032", new BigDecimal("17.4401000"), new BigDecimal("78.3489000"), "Bio-Diversity Flyover"));
            Location l4 = locationRepository.save(new Location("Financial District (Nanakramguda)", "500032", new BigDecimal("17.4156000"), new BigDecimal("78.3427000"), "WaveRock SEZ"));
            Location l5 = locationRepository.save(new Location("Jubilee Hills (Check Post / Rd 36)", "500033", new BigDecimal("17.4319000"), new BigDecimal("78.4073000"), "Check Post Metro"));
            Location l6 = locationRepository.save(new Location("Banjara Hills (Road No. 1)", "500034", new BigDecimal("17.4156000"), new BigDecimal("78.4354000"), "City Centre Mall"));
            Location l7 = locationRepository.save(new Location("Panjagutta Junction", "500082", new BigDecimal("17.4265000"), new BigDecimal("78.4526000"), "Panjagutta Central & Flyover"));
            Location l8 = locationRepository.save(new Location("Ameerpet (Metro Interchange)", "500016", new BigDecimal("17.4375000"), new BigDecimal("78.4482000"), "Ameerpet Metro Junction"));
            Location l9 = locationRepository.save(new Location("Kukatpally (KPHB Colony)", "500072", new BigDecimal("17.4938000"), new BigDecimal("78.3995000"), "JNTU / Forum Mall"));
            Location l10 = locationRepository.save(new Location("Secunderabad (Railway Station)", "500003", new BigDecimal("17.4399000"), new BigDecimal("78.4983000"), "Secunderabad Terminal"));
            Location l11 = locationRepository.save(new Location("Begumpet (Airport Road)", "500016", new BigDecimal("17.4448000"), new BigDecimal("78.4688000"), "Old Airport"));
            Location l12 = locationRepository.save(new Location("Mehdipatnam (Sarojini Devi)", "500028", new BigDecimal("17.3916000"), new BigDecimal("78.4414000"), "PVNR Flyover Entry"));
            Location l13 = locationRepository.save(new Location("Shamshabad (RGIA Airport)", "500409", new BigDecimal("17.2403000"), new BigDecimal("78.4294000"), "Rajiv Gandhi Airport"));
            Location l14 = locationRepository.save(new Location("Uppal (Cricket Stadium)", "500039", new BigDecimal("17.4018000"), new BigDecimal("78.5602000"), "Cricket Stadium"));
            Location l15 = locationRepository.save(new Location("Dilsukhnagar (Bus Station)", "500060", new BigDecimal("17.3688000"), new BigDecimal("78.5247000"), "Metro Terminal"));
            Location l16 = locationRepository.save(new Location("Charminar (Historic Old City)", "500002", new BigDecimal("17.3616000"), new BigDecimal("78.4747000"), "Charminar"));

            log.info("Seeding Hyderabad road network segments...");
            createRoad("Cyber Towers - Mindspace Link", "HYD-RD-01", l1, l2, Road.RoadType.ARTERIAL, new BigDecimal("2.50"), 50, TrafficSnapshot.CongestionLevel.MODERATE, new BigDecimal("34.50"));
            createRoad("Road No. 36 Madhapur - Jubilee Hills", "HYD-RD-02", l2, l5, Road.RoadType.ARTERIAL, new BigDecimal("4.20"), 50, TrafficSnapshot.CongestionLevel.HEAVY, new BigDecimal("21.00"));
            createRoad("Road No. 10 Jubilee - Banjara Link", "HYD-RD-03", l5, l6, Road.RoadType.ARTERIAL, new BigDecimal("3.80"), 45, TrafficSnapshot.CongestionLevel.MODERATE, new BigDecimal("32.00"));
            Road r4 = createRoad("Banjara Hills Rd 1 to Panjagutta", "HYD-RD-04", l6, l7, Road.RoadType.ARTERIAL, new BigDecimal("2.10"), 40, TrafficSnapshot.CongestionLevel.SEVERE, new BigDecimal("12.50"));
            createRoad("Panjagutta - Ameerpet Corridor (NH-65)", "HYD-RD-05", l7, l8, Road.RoadType.ARTERIAL, new BigDecimal("2.30"), 45, TrafficSnapshot.CongestionLevel.SEVERE, new BigDecimal("14.00"));
            createRoad("Ameerpet - Kukatpally Highway (NH-65)", "HYD-RD-06", l8, l9, Road.RoadType.ARTERIAL, new BigDecimal("7.50"), 60, TrafficSnapshot.CongestionLevel.HEAVY, new BigDecimal("28.00"));
            createRoad("KPHB - Hitec City MMTS Link Road", "HYD-RD-07", l9, l1, Road.RoadType.SUB_ARTERIAL, new BigDecimal("5.80"), 50, TrafficSnapshot.CongestionLevel.LOW, new BigDecimal("46.00"));
            createRoad("Panjagutta - Begumpet (Raj Bhavan Rd)", "HYD-RD-08", l7, l11, Road.RoadType.ARTERIAL, new BigDecimal("3.20"), 50, TrafficSnapshot.CongestionLevel.MODERATE, new BigDecimal("35.00"));
            createRoad("Begumpet - Secunderabad (SP Road)", "HYD-RD-09", l11, l10, Road.RoadType.ARTERIAL, new BigDecimal("4.60"), 50, TrafficSnapshot.CongestionLevel.HEAVY, new BigDecimal("22.50"));
            createRoad("Hitec City - Gachibowli Main Rd", "HYD-RD-10", l1, l3, Road.RoadType.ARTERIAL, new BigDecimal("4.80"), 60, TrafficSnapshot.CongestionLevel.HEAVY, new BigDecimal("24.00"));
            createRoad("Gachibowli - Financial District (ISB Rd)", "HYD-RD-11", l3, l4, Road.RoadType.ARTERIAL, new BigDecimal("3.50"), 60, TrafficSnapshot.CongestionLevel.LOW, new BigDecimal("52.00"));
            createRoad("Nehru ORR Expressway (Gachibowli - Shamshabad)", "HYD-RD-12", l3, l13, Road.RoadType.EXPRESSWAY, new BigDecimal("28.50"), 100, TrafficSnapshot.CongestionLevel.LOW, new BigDecimal("92.00"));
            createRoad("Nehru ORR (Financial District - Airport)", "HYD-RD-13", l4, l13, Road.RoadType.EXPRESSWAY, new BigDecimal("26.00"), 100, TrafficSnapshot.CongestionLevel.LOW, new BigDecimal("94.00"));
            createRoad("PVNR Elevated Expressway (Mehdipatnam - Airport)", "HYD-RD-14", l12, l13, Road.RoadType.EXPRESSWAY, new BigDecimal("21.00"), 80, TrafficSnapshot.CongestionLevel.MODERATE, new BigDecimal("62.00"));
            createRoad("Banjara Hills Rd 12 to Mehdipatnam", "HYD-RD-15", l6, l12, Road.RoadType.ARTERIAL, new BigDecimal("3.90"), 45, TrafficSnapshot.CongestionLevel.MODERATE, new BigDecimal("30.00"));
            createRoad("Secunderabad - Uppal Corridor (via Habsiguda)", "HYD-RD-16", l10, l14, Road.RoadType.ARTERIAL, new BigDecimal("9.20"), 50, TrafficSnapshot.CongestionLevel.HEAVY, new BigDecimal("25.00"));
            createRoad("Uppal - Dilsukhnagar Inner Ring Road", "HYD-RD-17", l14, l15, Road.RoadType.ARTERIAL, new BigDecimal("7.80"), 50, TrafficSnapshot.CongestionLevel.MODERATE, new BigDecimal("33.00"));
            createRoad("Dilsukhnagar - Charminar (Chaderghat)", "HYD-RD-18", l15, l16, Road.RoadType.SUB_ARTERIAL, new BigDecimal("5.20"), 40, TrafficSnapshot.CongestionLevel.SEVERE, new BigDecimal("11.00"));
            createRoad("Charminar - Mehdipatnam Link", "HYD-RD-19", l16, l12, Road.RoadType.SUB_ARTERIAL, new BigDecimal("6.40"), 40, TrafficSnapshot.CongestionLevel.HEAVY, new BigDecimal("18.00"));
            createRoad("Secunderabad - Ameerpet Metro Line", "HYD-RD-20", l10, l8, Road.RoadType.ARTERIAL, new BigDecimal("6.50"), 50, TrafficSnapshot.CongestionLevel.MODERATE, new BigDecimal("31.00"));

            // Create sample incident
            if (incidentRepository.count() == 0) {
                TrafficIncident inc = new TrafficIncident();
                inc.setRoad(r4);
                inc.setIncidentType(TrafficIncident.IncidentType.ACCIDENT);
                inc.setSeverity(TrafficIncident.Severity.CRITICAL);
                inc.setDescription("Multi-vehicle collision near Panjagutta Central Mall flyover ascent.");
                inc.setStatus(TrafficIncident.Status.IN_PROGRESS);
                inc.setImpactDelayMinutes(25);
                incidentRepository.save(inc);
            }
        }
    }

    private Road createRoad(String name, String code, Location start, Location end, Road.RoadType type, BigDecimal dist, int speedLimit, TrafficSnapshot.CongestionLevel level, BigDecimal currentSpeed) {
        Road r = new Road();
        r.setRoadName(name);
        r.setRoadCode(code);
        r.setStartLocation(start);
        r.setEndLocation(end);
        r.setRoadType(type);
        r.setDistanceKm(dist);
        r.setSpeedLimitKmh(speedLimit);
        r.setLanes(4);
        r.setIsToll(type == Road.RoadType.EXPRESSWAY);
        r.setIsBidirectional(true);
        r = roadRepository.save(r);

        TrafficSnapshot s = new TrafficSnapshot();
        s.setRoad(r);
        s.setCongestionLevel(level);
        s.setAverageSpeedKmh(currentSpeed);
        s.setVehicleCount(450);
        BigDecimal travelTime = dist.divide(currentSpeed, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("60")).setScale(2, java.math.RoundingMode.HALF_UP);
        s.setEstimatedTravelTimeMin(travelTime);
        s.setRecordedAt(LocalDateTime.now());
        s.setDataSource(TrafficSnapshot.DataSource.DEMO_SIMULATED);
        snapshotRepository.save(s);

        return r;
    }
}
