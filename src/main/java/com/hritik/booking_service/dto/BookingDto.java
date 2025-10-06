package com.hritik.booking_service.dto;


import com.hritik.entity_service.model.ExactLocation;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Long passengerId;

    private ExactLocation startLocation;

    private ExactLocation endLocation;

}
