package com.trafficiq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class TrafficiqApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring application context boots properly with H2
    }
}
