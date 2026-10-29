package com.trafficiq.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "location_name", nullable = false, unique = true, length = 150)
    private String locationName;

    @Column(name = "area_code", nullable = false, length = 50)
    private String areaCode;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 200)
    private String landmark;

    @Column(nullable = false, length = 50)
    private String city = "Hyderabad";

    public Location() {}

    public Location(String locationName, String areaCode, BigDecimal latitude, BigDecimal longitude, String landmark) {
        this.locationName = locationName;
        this.areaCode = areaCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.landmark = landmark;
    }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String areaCode) { this.areaCode = areaCode; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
