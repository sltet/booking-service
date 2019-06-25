package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.model.CustomerReservation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReservationService {

    Mono<CustomerReservation> findById(Long reservationId);

    Mono<CustomerReservation> create(CustomerReservation customerReservation);

    Mono<CustomerReservation> update(CustomerReservation customerReservation);

    Mono<CustomerReservation> cancel(Long reservationId);
}
