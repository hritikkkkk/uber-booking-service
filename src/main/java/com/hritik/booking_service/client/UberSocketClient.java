package com.hritik.booking_service.client;


import com.hritik.booking_service.dto.RideRequestDto;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UberSocketClient {

    @POST("api/socket/newride")
    Call<Boolean> raiseRideRequest(@Body RideRequestDto requestDto);
}