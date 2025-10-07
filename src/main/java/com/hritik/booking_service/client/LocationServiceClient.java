package com.hritik.booking_service.client;

import com.hritik.booking_service.dto.DriverLocationResponse;
import com.hritik.booking_service.dto.NearbyDriversRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationServiceClient {
    @POST("/api/v1/locations/drivers/nearby")
    Call<DriverLocationResponse> getNearbyDrivers(@Body NearbyDriversRequestDto request);
}
