package com.hritik.booking_service.dto;

import com.hritik.entity_service.model.Driver;
import com.hritik.entity_service.model.ExactLocation;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponseDto {
    private Long bookingId;
    private String bookingStatus;
    private Optional<Driver> driver;

}
