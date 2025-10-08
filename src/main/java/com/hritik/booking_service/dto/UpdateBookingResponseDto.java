package com.hritik.booking_service.dto;

import lombok.*;

import com.hritik.entity_service.model.BookingStatus;
import com.hritik.entity_service.model.Driver;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingResponseDto {
    private Long bookingId;
    private BookingStatus status;
    private Optional<DriverDto> driver;
}
