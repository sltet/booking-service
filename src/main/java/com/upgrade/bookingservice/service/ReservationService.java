package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.model.CustomerReservation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReservationService {

    public Mono<CustomerReservation> findById(Long reservationId);

    public Mono<CustomerReservation> create(CustomerReservation customerReservation);

    public Mono<CustomerReservation> update(CustomerReservation customerReservation);

    public Mono<CustomerReservation> cancel(Long reservationId);

    public Flux<LocalDate> findAvailabilitiesBetween(LocalDate start, LocalDate end);
}
