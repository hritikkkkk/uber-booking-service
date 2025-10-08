package com.hritik.booking_service.repository;

import com.hritik.booking_service.dto.BookingSummaryDto;
import com.hritik.entity_service.model.Booking;
import com.hritik.entity_service.model.BookingStatus;
import com.hritik.entity_service.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.driver = :driver, b.bookingStatus = :status WHERE b.id = :bookingId")
    int updateDriverAndStatus(@Param("bookingId") Long bookingId,
                              @Param("driver") Driver driver,
                              @Param("status") BookingStatus status);

    boolean existsById(Long id);

    @Query("SELECT new com.hritik.booking_service.dto.BookingSummaryDto(b.id, b.bookingStatus) " +
            "FROM Booking b WHERE b.id = :bookingId")
    Optional<BookingSummaryDto> findBookingSummaryById(@Param("bookingId") Long bookingId);

}
