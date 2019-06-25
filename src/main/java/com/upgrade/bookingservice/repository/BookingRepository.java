package com.upgrade.bookingservice.repository;

import com.upgrade.bookingservice.model.Booking;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookingRepository {

    Booking save(Booking booking);

    Booking update(Booking booking);

    void cancel(Booking booking);

    Flux<LocalDate> findAvailabilitiesBetween(LocalDateTime arrivalDate, LocalDateTime departureDate);

    boolean contains(LocalDateTime bookingDate);

    boolean isRangeAvailable(LocalDateTime arrivalDate, LocalDateTime departureDate);

}
