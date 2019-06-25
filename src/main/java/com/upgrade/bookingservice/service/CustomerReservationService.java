package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.exception.NotFoundException;
import com.upgrade.bookingservice.exception.UnprocessableEntityException;
import com.upgrade.bookingservice.model.CustomerReservation;
import com.upgrade.bookingservice.model.Status;
import com.upgrade.bookingservice.repository.CustomerReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerReservationService implements ReservationService {


    private final BookingService bookingService;
    private final CustomerReservationRepository customerReservationRepository;

    @Override
    public Mono<CustomerReservation> findById(Long reservationId) {
        return Mono.justOrEmpty(customerReservationRepository.findById(reservationId))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Couldn't find reservation with id %s", reservationId))));
    }

    @Override
    synchronized public Mono<CustomerReservation> create(CustomerReservation reservation) {
        return Mono.just(reservation)
                .doOnNext(res -> res.setStatus(Status.COMPLETED))
                .map(customerReservationRepository::save)
                .flatMap(bookingService::createBooking)
                .switchIfEmpty(Mono.error(new UnprocessableEntityException("Requested range of dates is unavailable")));
    }

    @Override
    synchronized public Mono<CustomerReservation> update(CustomerReservation reservationUpdate) {
        return Mono.justOrEmpty(customerReservationRepository.findById(reservationUpdate.getId()))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Couldn't find reservation with id %s", reservationUpdate.getId()))))
                .filter(reservation -> reservation.getStatus().equals(Status.COMPLETED))
                .switchIfEmpty(Mono.error(new UnprocessableEntityException(String.format("Reservation with id %s already cancelled", reservationUpdate.getId()))))
                .doOnNext(reservation -> {
                    reservation.setArrivalDate(reservationUpdate.getArrivalDate());
                    reservation.setDepartureDate(reservationUpdate.getDepartureDate());
                })
                .flatMap(bookingService::updateBooking)
                .map(customerReservationRepository::save);
    }

    @Override
    public Mono<CustomerReservation> cancel(Long reservationId) {
        return Mono.justOrEmpty(customerReservationRepository.findById(reservationId))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Couldn't find reservation with id %s", reservationId))))
                .filter(reservation -> reservation.getStatus().equals(Status.COMPLETED))
                .switchIfEmpty(Mono.error(new UnprocessableEntityException(String.format("Reservation with id %s already cancelled", reservationId))))
                .flatMap(bookingService::cancelBooking)
                .doOnNext(reservation -> reservation.setStatus(Status.CANCELLED))
                .map(customerReservationRepository::save);
    }

}
