package com.hritik.booking_service.controller;

import com.hritik.booking_service.dto.BookingDto;
import com.hritik.booking_service.dto.BookingResponseDto;
import com.hritik.booking_service.dto.UpdateBookingRequestDto;
import com.hritik.booking_service.dto.UpdateBookingResponseDto;
import com.hritik.booking_service.service.BookingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/booking")
public class BookingController {
    private final BookingServiceImpl bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingDto bookingDto) {

        return new ResponseEntity<>(bookingService.createBooking(bookingDto), HttpStatus.CREATED);
    }

    @PatchMapping("{BookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(@RequestBody UpdateBookingRequestDto updateBookingRequestDto, @PathVariable Long BookingId) {
        return new ResponseEntity<>(bookingService.updateBooking(updateBookingRequestDto, BookingId), HttpStatus.OK);
    }
}
