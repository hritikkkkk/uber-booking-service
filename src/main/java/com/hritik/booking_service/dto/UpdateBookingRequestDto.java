package com.hritik.booking_service.dto;

import com.hritik.entity_service.model.BookingStatus;
import lombok.*;

import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingRequestDto {
    private BookingStatus bookingStatus;
    private Optional<Long> driverId;

}
