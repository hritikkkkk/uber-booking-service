package com.hritik.booking_service.dto;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocationDto {

    @SerializedName("driver_id")
    private String driverId;

    private double latitude;
    private double longitude;

    @SerializedName("distance_km")
    private double distanceKm;
}
