package com.hritik.booking_service.repository;

import com.hritik.booking_service.dto.DriverDto;
import com.hritik.entity_service.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverRepository extends JpaRepository<Driver,Long> {
    boolean existsById(Long id);


    @Query("SELECT new com.hritik.booking_service.dto.DriverDto(" +
            "d.id, d.name, d.licenseNumber,d.isAvailable) " +
            "FROM Driver d WHERE d.id = :driverId")
    DriverDto findDriverDtoById(@Param("driverId") Long driverId);

    @Modifying
    @Query("UPDATE Driver d SET d.isAvailable = false WHERE d.id = :driverId")
    void markDriverUnavailable(@Param("driverId") Long driverId);

}
