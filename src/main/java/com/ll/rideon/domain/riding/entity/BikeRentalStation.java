package com.ll.rideon.domain.riding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bike_rental_station")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BikeRentalStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rental_station_name", nullable = false)
    private String rentalStationName;

    @Column(name = "rental_station_type", length = 100)
    private String rentalStationType;

    @Column(name = "address_street", columnDefinition = "TEXT")
    private String addressStreet;

    @Column(name = "address_jibun", columnDefinition = "TEXT")
    private String addressJibun;

    @Column(name = "latitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column(name = "operation_start_time")
    private LocalDateTime operationStartTime;

    @Column(name = "operation_end_time")
    private LocalDateTime operationEndTime;

    @Column(name = "closed_days")
    private LocalDateTime closedDays;

    @Column(name = "fee_type", length = 50)
    private String feeType;

    @Column(name = "rental_fee", columnDefinition = "TEXT")
    private String rentalFee;

    @Column(name = "bicycle_count")
    private Integer bicycleCount;

    @Column(name = "parking_spots")
    private Integer parkingSpots;

    @Column(name = "air_pump_available")
    private Boolean airPumpAvailable;

    @Column(name = "air_pump_type", length = 100)
    private String airPumpType;

    @Column(name = "repair_station_available")
    private Boolean repairStationAvailable;

    @Column(name = "management_phone", length = 50)
    private String managementPhone;

    @Column(name = "management_name")
    private String managementName;

    @Column(name = "data_date")
    private LocalDate dataDate;

    @Column(name = "provider_code", length = 50)
    private String providerCode;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "province", nullable = false, length = 100)
    private String province;

    @Builder
    public BikeRentalStation(String rentalStationName, String rentalStationType, 
                           String addressStreet, String addressJibun,
                           BigDecimal latitude, BigDecimal longitude,
                           String province) {
        this.rentalStationName = rentalStationName;
        this.rentalStationType = rentalStationType;
        this.addressStreet = addressStreet;
        this.addressJibun = addressJibun;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
    }
}
