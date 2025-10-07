package com.hritik.booking_service.repository;

import com.hritik.entity_service.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    boolean existsById(Long id);
}
