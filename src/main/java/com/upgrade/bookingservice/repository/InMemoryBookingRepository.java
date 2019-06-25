package com.upgrade.bookingservice.repository;

import com.upgrade.bookingservice.exception.UnprocessableEntityException;
import com.upgrade.bookingservice.model.Booking;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private Map<LocalDateTime, Long> bookings = new HashMap<>();

    @Override
    public void save(Booking booking) {
        getDatesBetween(booking.getArrivalDate(), booking.getDepartureDate())
                .forEach(date -> this.bookings.put(date, booking.getReservationId()));
    }

    @Override
    public void update(Booking booking) {
        getDatesInConflict(booking.getReservationId(), booking.getArrivalDate(), booking.getDepartureDate())
                .findAny()
                .ifPresent(localDateTime -> {
                    throw new UnprocessableEntityException(String.format("Requested range of dates is in conflict on day %s", localDateTime.toLocalDate()));
                });

        Stream<LocalDateTime> datesBetween = getDatesBetween(booking.getArrivalDate(), booking.getDepartureDate());
        datesBetween.forEach(bookings::remove);
        datesBetween.forEach(date -> this.bookings.put(date, booking.getReservationId()));
    }

    @Override
    public void cancel(Booking booking) {
        getDatesBetween(booking.getArrivalDate(), booking.getDepartureDate())
                .forEach(bookings::remove);
    }

    @Override
    public Long findBookingReservationByDate(LocalDateTime bookingDate) {
        return bookings.get(bookingDate);
    }

    @Override
    public Flux<LocalDate> findAvailabilitiesBetween(LocalDateTime arrivalDate, LocalDateTime departureDate) {
        return Flux.fromStream(getDatesBetween(arrivalDate, departureDate))
                .subscribeOn(Schedulers.elastic())
                .filter(localDateTime -> !bookings.containsKey(localDateTime))
                .map(LocalDateTime::toLocalDate);
    }

    @Override
    public boolean contains(LocalDateTime bookingDate) {
        return this.bookings.containsKey(bookingDate);
    }

    @Override
    public boolean isRangeAvailable(LocalDateTime arrival, LocalDateTime departure) {
        return getDatesBetween(arrival, departure)
                .noneMatch(this::contains);
    }

    @Override
    public void removeByDate(LocalDateTime bookingDate) {
        this.bookings.remove(bookingDate);
    }

    private Stream<LocalDateTime> getDatesBetween(LocalDateTime arrival, LocalDateTime departure) {
        return IntStream.iterate(0, i -> i + 1)
                .limit(Math.toIntExact(ChronoUnit.DAYS.between(arrival, departure)))
                .mapToObj(arrival::plusDays);
    }

    private Stream<LocalDateTime> getDatesInConflict(Long reservationId, LocalDateTime arrival, LocalDateTime departure) {
        return getDatesBetween(arrival, departure)
                .filter(localDateTime -> bookings.containsKey(localDateTime) && bookings.get(localDateTime) != reservationId);
    }
}
