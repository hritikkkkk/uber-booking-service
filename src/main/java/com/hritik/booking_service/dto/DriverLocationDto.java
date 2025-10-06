package com.hritik.booking_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocationDto {

    @JsonProperty("driver_id")
    private String driverId;

    private double latitude;
    private double longitude;

    @JsonProperty("distance_km")
    private double distanceKm;
}
