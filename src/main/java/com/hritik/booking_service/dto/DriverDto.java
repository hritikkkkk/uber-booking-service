package com.hritik.booking_service.dto;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverDto {
    private Long id;
    private String name;
    private String licenseNumber;

    private Boolean isAvailable;
}
