package com.hritik.booking_service.dto;

import com.hritik.entity_service.model.BookingStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingSummaryDto {
    private Long id;
    private BookingStatus bookingStatus;

}

