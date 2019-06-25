package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.exception.UnprocessableEntityException;
import com.upgrade.bookingservice.model.Booking;
import com.upgrade.bookingservice.model.CustomerReservation;
import com.upgrade.bookingservice.repository.BookingRepository;
import com.upgrade.bookingservice.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CustomerBookingService implements BookingService {

    private final BookingRepository bookingRepository;


    @Override
    public Mono<CustomerReservation> createBooking(CustomerReservation reservation) {
        return Mono.just(reservation)
                .filter(res -> bookingRepository.isRangeAvailable(res.getArrivalDate(), res.getDepartureDate()))
                .switchIfEmpty(Mono.error(new UnprocessableEntityException("Requested range of dates is unavailable")))
                .map(convertReservationToBooking)
                .doOnNext(bookingRepository::save)
                .thenReturn(reservation);
    }

    @Override
    public Mono<CustomerReservation> updateBooking(CustomerReservation reservation) {
        return Mono.just(reservation)
                .map(convertReservationToBooking)
                .doOnNext(bookingRepository::update)
                .thenReturn(reservation);
    }

    @Override
    public Mono<CustomerReservation> cancelBooking(CustomerReservation reservation) {
        return Mono.just(reservation)
                .map(convertReservationToBooking)
                .doOnNext(bookingRepository::cancel)
                .thenReturn(reservation);
    }

    @Override
    public Flux<LocalDate> findAvailabilitiesBetween(LocalDate start, LocalDate end) {
        LocalDateTime arrivalDate = LocalDateTime.of(start, Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(end, Constants.DEFAULT_CHECK_IN_TIME);

        return bookingRepository.findAvailabilitiesBetween(arrivalDate, departureDate);
    }

    Function<CustomerReservation, Booking> convertReservationToBooking = reservation -> Booking.builder()
            .arrivalDate(reservation.getArrivalDate())
            .departureDate(reservation.getDepartureDate())
            .reservationId(reservation.getId())
            .build();

}
