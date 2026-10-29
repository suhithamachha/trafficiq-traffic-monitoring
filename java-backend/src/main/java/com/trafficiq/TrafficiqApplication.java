package com.trafficiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * TRAFFICIQ — Real-Time Traffic Monitoring and Intelligent Route Database
 * Main Spring Boot Application Entry Point.
 */
@SpringBootApplication
@EnableTransactionManagement
public class TrafficiqApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrafficiqApplication.class, args);
    }
}
