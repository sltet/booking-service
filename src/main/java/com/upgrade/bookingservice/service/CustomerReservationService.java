package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.exception.BadRequestException;
import com.upgrade.bookingservice.exception.NotFoundException;
import com.upgrade.bookingservice.exception.UnprocessableEntityException;
import com.upgrade.bookingservice.model.CustomerReservation;
import com.upgrade.bookingservice.model.Status;
import com.upgrade.bookingservice.repository.CustomerReservationRepository;
import com.upgrade.bookingservice.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomerReservationService implements ReservationService {

    private Map<LocalDateTime, Long> bookings = new HashMap<>();

    private final CustomerReservationRepository customerReservationRepository;

    @Override
    public Mono<CustomerReservation> findById(Long reservationId) {
        return Mono.justOrEmpty(customerReservationRepository.findById(reservationId))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Couldn't find reservation with id %s", reservationId))));
    }

    @Override
    synchronized public Mono<CustomerReservation> create(CustomerReservation reservation) {
        return Mono.just(reservation)
                .filter(res -> isRangeEmpty(res.getArrivalDate(), res.getDepartureDate()))
                .doOnNext(res -> res.setStatus(Status.COMPLETED))
                .map(customerReservationRepository::save)
                .doOnNext(savedReservation -> {
                    getDatesBetween(savedReservation.getArrivalDate(), savedReservation.getDepartureDate())
                        .forEach(unavailableDate -> bookings.put(unavailableDate, savedReservation.getId()));
                })
                .switchIfEmpty(Mono.error(new UnprocessableEntityException("Requested range of dates is unavailable")));
    }

    @Override
    synchronized public Mono<CustomerReservation> update(CustomerReservation reservationUpdate) {

        getDatesInConflict(reservationUpdate.getId(), reservationUpdate.getArrivalDate(), reservationUpdate.getDepartureDate())
                .findAny()
                .ifPresent(localDateTime -> {
                    throw new UnprocessableEntityException(String.format("Requested range of dates is in conflict on day %s", localDateTime.toLocalDate()));
                });

        return Mono.justOrEmpty(customerReservationRepository.findById(reservationUpdate.getId()))
                .filter(reservation -> reservation.getStatus().equals(Status.COMPLETED))
                .switchIfEmpty(Mono.error(new BadRequestException(String.format("Reservation with id %s already cancelled", reservationUpdate.getId()))))
                .doOnNext(oldReservation -> {
                        getDatesBetween(oldReservation.getArrivalDate(), oldReservation.getDepartureDate())
                                .forEach(date -> bookings.remove(date));
                })
                .doOnNext(reservation -> {
                    reservation.setArrivalDate(reservationUpdate.getArrivalDate());
                    reservation.setDepartureDate(reservationUpdate.getDepartureDate());
                })
                .map(customerReservationRepository::save)
                .doOnNext(savedReservation -> {
                    getDatesBetween(savedReservation.getArrivalDate(), savedReservation.getDepartureDate())
                            .forEach(date -> bookings.put(date, savedReservation.getId()));
                });
    }

    @Override
    public Mono<CustomerReservation> cancel(Long reservationId) {
        return Mono.justOrEmpty(customerReservationRepository.findById(reservationId))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Couldn't find reservation with id %s", reservationId))))
                .filter(reservation -> reservation.getStatus().equals(Status.COMPLETED))
                .switchIfEmpty(Mono.error(new BadRequestException(String.format("Reservation with id %s already cancelled", reservationId))))
                .doOnNext(reservation -> {
                    getDatesBetween(reservation.getArrivalDate(), reservation.getDepartureDate())
                            .forEach(unavailableDate -> bookings.remove(unavailableDate));
                    reservation.setStatus(Status.CANCELLED);
                })
                .map(customerReservationRepository::save);
    }

    private boolean isRangeEmpty(LocalDateTime arrival, LocalDateTime departure) {
        List<LocalDateTime> requestedDates = getDatesBetween(arrival, departure).collect(Collectors.toList());
        for (LocalDateTime requestedDate : requestedDates) {
            if(bookings.containsKey(requestedDate)){
                return false;
            }
        }
        return true;
    }

    public Flux<LocalDate> findAvailabilitiesBetween(LocalDate start, LocalDate end) {
        LocalDateTime startDate = LocalDateTime.of(start, Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime endDate = LocalDateTime.of(end, Constants.DEFAULT_CHECK_IN_TIME);

        return Flux.fromStream(getDatesBetween(startDate, endDate))
                .subscribeOn(Schedulers.elastic())
                .filter(localDateTime -> !bookings.containsKey(localDateTime))
                .map(LocalDateTime::toLocalDate);
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
