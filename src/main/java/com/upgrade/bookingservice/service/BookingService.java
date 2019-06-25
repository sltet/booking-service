package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.model.CustomerReservation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookingService {
    Mono<CustomerReservation> createBooking(CustomerReservation reservation);

    Mono<CustomerReservation> updateBooking(CustomerReservation reservation);

    Mono<CustomerReservation> cancelBooking(CustomerReservation reservation);

    Flux<LocalDate> findAvailabilitiesBetween(LocalDate start, LocalDate end);
}
