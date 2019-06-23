package com.upgrade.bookingservice.repository;

import com.upgrade.bookingservice.model.CustomerReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerReservationRepository extends JpaRepository<CustomerReservation, Long> {
}
