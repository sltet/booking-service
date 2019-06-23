package com.upgrade.bookingservice.controller;

import com.upgrade.bookingservice.controller.dto.ReservationRequest;
import com.upgrade.bookingservice.controller.dto.CustomerReservationResponse;
import com.upgrade.bookingservice.controller.dto.UpdateReservationRequest;
import com.upgrade.bookingservice.converter.ReservationConverter;
import com.upgrade.bookingservice.service.ReservationService;
import com.upgrade.bookingservice.validator.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {

    private final ReservationValidator reservationValidator;
    private final ReservationService reservationService;
    private final ReservationConverter reservationConverter;

    @Override
    @GetMapping("/{reservationId}")
    public Mono<ResponseEntity<CustomerReservationResponse>> findReservationById(@PathVariable("reservationId") Long reservationId) {
        return reservationService.findById(reservationId)
                .map(reservationConverter::convert)
                .map(ResponseEntity::ok);
    }

    @Override
    @PostMapping
    public Mono<ResponseEntity<CustomerReservationResponse>> createReservation(@Valid @RequestBody ReservationRequest reservationRequest) {
        return Mono.just(reservationConverter.create(reservationRequest))
                .flatMap(reservationService::create)
                .map(reservationConverter::convert)
                .map(reservation -> new ResponseEntity<>(reservation, HttpStatus.CREATED));
    }

    @Override
    @PutMapping("/{reservationId}")
    public Mono<ResponseEntity<CustomerReservationResponse>> updateReservation(@PathVariable("reservationId") Long reservationId, @Valid @RequestBody UpdateReservationRequest updateReservationRequest) {
        return Mono.just(reservationConverter.update(reservationId, updateReservationRequest))
                .flatMap(reservationService::update)
                .map(reservationConverter::convert)
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(path = "/{reservationId}/cancel")
    public Mono<ResponseEntity<CustomerReservationResponse>> cancelReservation(@PathVariable("reservationId") Long reservationId) {
        return reservationService.cancel(reservationId)
                .map(reservationConverter::convert)
                .map(ResponseEntity::ok);
    }

    @InitBinder(value = {"reservationRequest", "updateReservationRequest"})
    public void init(WebDataBinder binder) {
        binder.addValidators(reservationValidator);
    }
}
