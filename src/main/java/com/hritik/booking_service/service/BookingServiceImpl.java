package com.hritik.booking_service.service;

import com.hritik.booking_service.client.LocationServiceClient;
import com.hritik.booking_service.dto.*;
import com.hritik.booking_service.exception.ResourceNotFoundException;
import com.hritik.booking_service.repository.BookingRepository;
import com.hritik.booking_service.repository.PassengerRepository;
import com.hritik.entity_service.model.Booking;
import com.hritik.entity_service.model.BookingStatus;
import com.hritik.entity_service.model.Passenger;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import retrofit2.Callback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final LocationServiceClient locationServiceClient;

    public BookingServiceImpl(PassengerRepository passengerRepository,
                              BookingRepository bookingRepository,
                              EntityManager entityManager,
                              LocationServiceClient locationServiceClient) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.entityManager = entityManager;
        this.locationServiceClient = locationServiceClient;
    }

    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto) {
        boolean exists = passengerRepository.existsById(bookingDto.getPassengerId());
        if (!exists) {
            throw new ResourceNotFoundException(
                    "Passenger with id " + bookingDto.getPassengerId() + " not found"
            );
        }

        Passenger passenger = entityManager.getReference(Passenger.class, bookingDto.getPassengerId());

        Booking booking = Booking.builder()
                .passenger(passenger)
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDto.getStartLocation())
                .endLocation(bookingDto.getEndLocation())
                .build();

        Booking newBooking = bookingRepository.save(booking);

        NearbyDriversRequestDto request = NearbyDriversRequestDto.builder()
                .longitude(bookingDto.getStartLocation().getLongitude())
                .latitude(bookingDto.getStartLocation().getLatitude())
                .build();


        // Asynchronous Retrofit call
        locationServiceClient.getNearbyDrivers(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DriverLocationResponse> call, Response<DriverLocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DriverLocationDto> drivers = response.body().getData();
                    drivers.forEach(driver -> {
                        System.out.println("**************");
                        System.out.print(driver.getDriverId() + " ");
                        System.out.print(driver.getLatitude() + " ");
                        System.out.print(driver.getLongitude() + " ");
                        System.out.println(driver.getDistanceKm());
                        System.out.println("**************");
                    });
                }
            }

            @Override
            public void onFailure(Call<DriverLocationResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return BookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }
}
