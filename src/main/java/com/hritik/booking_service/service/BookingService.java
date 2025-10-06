package com.hritik.booking_service.service;

import com.hritik.booking_service.dto.BookingDto;
import com.hritik.booking_service.dto.BookingResponseDto;

public interface BookingService {
    public BookingResponseDto createBooking(BookingDto bookingDto);
}
