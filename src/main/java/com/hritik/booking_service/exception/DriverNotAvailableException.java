package com.hritik.booking_service.exception;

public class DriverNotAvailableException extends RuntimeException {
    public DriverNotAvailableException(String message) {
        super(message);
    }
}
