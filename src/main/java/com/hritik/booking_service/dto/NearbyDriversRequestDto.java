package com.hritik.booking_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearbyDriversRequestDto {
    private Double longitude;
    private Double latitude;
}
