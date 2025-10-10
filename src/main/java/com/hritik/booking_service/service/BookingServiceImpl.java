package com.hritik.booking_service.service;

import com.hritik.booking_service.client.LocationServiceClient;
import com.hritik.booking_service.client.UberSocketClient;
import com.hritik.booking_service.dto.*;
import com.hritik.booking_service.exception.DriverNotAvailableException;
import com.hritik.booking_service.exception.ResourceNotFoundException;
import com.hritik.booking_service.repository.BookingRepository;
import com.hritik.booking_service.repository.DriverRepository;
import com.hritik.booking_service.repository.PassengerRepository;
import com.hritik.entity_service.model.Booking;
import com.hritik.entity_service.model.BookingStatus;
import com.hritik.entity_service.model.Driver;
import com.hritik.entity_service.model.Passenger;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import retrofit2.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

@Service
public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final LocationServiceClient locationServiceClient;
    private final DriverRepository driverRepository;

    private final UberSocketClient uberSocketClient;

    private final KafkaProducerService kafkaProducerService;

    public BookingServiceImpl(PassengerRepository passengerRepository,
                              BookingRepository bookingRepository,
                              EntityManager entityManager,
                              LocationServiceClient locationServiceClient,
                              DriverRepository driverRepository, UberSocketClient uberSocketClient, KafkaProducerService kafkaProducerService) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.entityManager = entityManager;
        this.locationServiceClient = locationServiceClient;
        this.driverRepository = driverRepository;
        this.uberSocketClient=uberSocketClient;
        this.kafkaProducerService = kafkaProducerService;
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
                try {
                    List<DriverLocationDto> drivers = response.body().getData();
                    List driverIds = drivers.stream()
                            .map(driver -> Long.parseLong(driver.getDriverId()))
                            .collect(Collectors.toList());
                    RideRequestDto rideRequest = RideRequestDto.builder()
                            .passengerId(bookingDto.getPassengerId())
                            .startLocation(bookingDto.getStartLocation())
                            .endLocation(bookingDto.getEndLocation())
                            .driverIds(driverIds)
                            .bookingId(newBooking.getId())
                            .build();
                    raiseRideRequestAsync(RideRequestDto.builder().passengerId(bookingDto.getPassengerId()).bookingId(newBooking.getId()).build());
                    kafkaProducerService.sendRideRequest(rideRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
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

    @Override
    @Transactional
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto dto, Long bookingId) {

        BookingSummaryDto bookingSummary = bookingRepository.findBookingSummaryById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + bookingId + " not found"));


        Long driverId = dto.getDriverId()
                .orElseThrow(() -> new IllegalArgumentException("Driver ID is required"));

        DriverDto driverDto = driverRepository.findDriverDtoById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + driverId + " not found"));

        if (!driverDto.getIsAvailable()) {
            throw new DriverNotAvailableException("Driver is not available");
        }


        bookingRepository.updateDriverAndStatus(bookingId,
                entityManager.getReference(Driver.class, driverId),
                BookingStatus.SCHEDULED);


        driverRepository.markDriverUnavailable(driverId);

        return UpdateBookingResponseDto.builder()
                .bookingId(bookingSummary.getId())
                .status(bookingSummary.getBookingStatus())
                .driver(Optional.of(driverDto))
                .build();
    }

    private void raiseRideRequestAsync(RideRequestDto requestDto) throws IOException {
        Call<Boolean> call = uberSocketClient.raiseRideRequest(requestDto);

        System.out.println(call.request().url() + " " + call.request().method() + " " + call.request().headers());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                System.out.println(response.isSuccessful());
                System.out.println(response.message());
                if (response.isSuccessful() && response.body() != null) {
                    Boolean result = response.body();
                    System.out.println("Driver response is" + result.toString());

                } else {
                    System.out.println("Request for ride failed" + response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}