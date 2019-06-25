package com.upgrade.bookingservice.repository;

import com.upgrade.bookingservice.model.Booking;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookingRepository {

    public void save(Booking booking);

    public void update(Booking booking);

    public void cancel(Booking booking);

    public Long findBookingReservationByDate(LocalDateTime bookingDate);

    public Flux<LocalDate> findAvailabilitiesBetween(LocalDateTime arrivalDate, LocalDateTime departureDate);

    public boolean contains(LocalDateTime bookingDate);

    public boolean isRangeAvailable(LocalDateTime arrivalDate, LocalDateTime departureDate);

    public void removeByDate(LocalDateTime bookingDate);
}
