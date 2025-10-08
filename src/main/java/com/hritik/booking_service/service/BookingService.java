package com.hritik.booking_service.service;

import com.hritik.booking_service.dto.BookingDto;
import com.hritik.booking_service.dto.BookingResponseDto;
import com.hritik.booking_service.dto.UpdateBookingRequestDto;
import com.hritik.booking_service.dto.UpdateBookingResponseDto;

public interface BookingService {
    BookingResponseDto createBooking(BookingDto bookingDto);

    UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto updateBookingRequestDto,Long BookingId);
}
