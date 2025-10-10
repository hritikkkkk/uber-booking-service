package com.hritik.booking_service.service;


import com.hritik.booking_service.dto.RideRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate kafkaTemplate;

    @Value("${kafka.topic.ride-request}")
    private String rideRequestTopic;

    public void sendRideRequest(RideRequestDto rideRequest) {
        log.info("Publishing ride request to Kafka: {}", rideRequest);

        CompletableFuture<SendResult> future =
                kafkaTemplate.send(rideRequestTopic, rideRequest.getBookingId().toString(), rideRequest);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Ride request sent successfully: bookingId={}, offset={}",
                        rideRequest.getBookingId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send ride request: bookingId={}",
                        rideRequest.getBookingId(), ex);
            }
        });
    }
}