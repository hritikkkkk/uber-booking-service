package com.hritik.booking_service.service;

import com.hritik.booking_service.dto.*;
import com.hritik.booking_service.exception.ResourceNotFoundException;
import com.hritik.booking_service.repository.BookingRepository;
import com.hritik.booking_service.repository.PassengerRepository;
import com.hritik.entity_service.model.Booking;
import com.hritik.entity_service.model.BookingStatus;
import com.hritik.entity_service.model.Passenger;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service

public class BookingServiceImpl implements BookingService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;

    private final RestTemplate restTemplate;

    private final EntityManager entityManager;

    public BookingServiceImpl(PassengerRepository passengerRepository,
                              BookingRepository bookingRepository, EntityManager entityManager) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.entityManager = entityManager;
        this.restTemplate = new RestTemplate();
    }

    private static final String LOCATION_SERVICE = "http://localhost:7777";

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


        ResponseEntity<DriverLocationResponse> response = restTemplate.postForEntity(
                LOCATION_SERVICE + "/api/v1/locations/drivers/nearby",
                request,
                DriverLocationResponse.class
        );


        List<DriverLocationDto> drivers = response.getBody().getData();
        for (DriverLocationDto driver : drivers) {
            System.out.println("**************");
            System.out.print(driver.getDriverId() + " ");
            System.out.print(driver.getLatitude() + " ");
            System.out.print(driver.getLongitude());
            System.out.println("**************");

        }
        return BookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();

    }
}
