package com.hritik.booking_service.dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocationResponse {
    private boolean success;
    private String message;
    private List<DriverLocationDto> data;
    private String timestamp;
}
