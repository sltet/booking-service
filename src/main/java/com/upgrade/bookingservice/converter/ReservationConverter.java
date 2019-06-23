package com.upgrade.bookingservice.converter;

import com.upgrade.bookingservice.controller.dto.ReservationRequest;
import com.upgrade.bookingservice.controller.dto.CustomerReservationResponse;
import com.upgrade.bookingservice.controller.dto.UpdateReservationRequest;
import com.upgrade.bookingservice.model.CustomerReservation;
import com.upgrade.bookingservice.util.Constants;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationConverter {

    public CustomerReservation create(ReservationRequest reservationRequest) {
        return CustomerReservation.builder()
                .fullName(reservationRequest.getFullName())
                .email(reservationRequest.getEmail())
                .arrivalDate(LocalDateTime.of(reservationRequest.getArrivalDate(), Constants.DEFAULT_CHECK_IN_TIME))
                .departureDate(LocalDateTime.of(reservationRequest.getDepartureDate(), Constants.DEFAULT_CHECK_OUT_TIME))
                .build();
    }

    public CustomerReservation update(Long reservationId, UpdateReservationRequest updateReservationRequest) {
        return CustomerReservation.builder()
                .id(reservationId)
                .arrivalDate(LocalDateTime.of(updateReservationRequest.getArrivalDate(), Constants.DEFAULT_CHECK_IN_TIME))
                .departureDate(LocalDateTime.of(updateReservationRequest.getDepartureDate(), Constants.DEFAULT_CHECK_OUT_TIME))
                .build();
    }

    public CustomerReservationResponse convert(CustomerReservation reservation) {
        return CustomerReservationResponse.builder()
                .id(reservation.getId())
                .fullName(reservation.getFullName())
                .email(reservation.getEmail())
                .arrivalDate(reservation.getArrivalDate())
                .departureDate(reservation.getDepartureDate())
                .status(reservation.getStatus().name())
                .build();
    }
}
