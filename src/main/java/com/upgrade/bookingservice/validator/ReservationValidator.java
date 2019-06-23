package com.upgrade.bookingservice.validator;

import com.upgrade.bookingservice.controller.dto.ReservationDTO;
import com.upgrade.bookingservice.util.Constants;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ReservationValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return ReservationDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ReservationDTO reservation = (ReservationDTO) obj;
        if(isMaximalNumberOfDaysConstraintViolated(reservation)) {
            errors.reject("The campsite can be reserved for max 3 days");
        }
        if(!isReservationPeriodValid(reservation)) {
            errors.reject("The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance");
        }
        if(isArrivalDateEqualsDepartureDate(reservation)) {
            errors.reject("Arrival date cannot be equal to departure date");
        }
        if(isArrivalDateAfterDepartureDate(reservation)) {
            errors.reject("Arrival date cannot be after departure date");
        }
    }

    private boolean isReservationPeriodValid(ReservationDTO reservation) {
        LocalDateTime arrival = LocalDateTime.of(reservation.getArrivalDate(), Constants.DEFAULT_CHECK_IN_TIME);
        return LocalDateTime.now().until(arrival, ChronoUnit.HOURS) > Constants.MINIMAL_RESERVATION_REQUEST_HOURS && arrival.minusMonths(Constants.MAXIMAL_RESERVATION_REQUEST_MONTHS).isBefore(LocalDateTime.now());
    }

    private boolean isArrivalDateAfterDepartureDate(ReservationDTO reservation) {
        return reservation.getArrivalDate().isAfter(reservation.getDepartureDate());
    }

    private boolean isArrivalDateEqualsDepartureDate(ReservationDTO reservation) {
        return reservation.getArrivalDate().isEqual(reservation.getDepartureDate());
    }

    private boolean isMaximalNumberOfDaysConstraintViolated(ReservationDTO reservation) {
        return ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate()) > Constants.MAXIMUM_DAYS_ALLOWED_FOR_A_RESERVATION;
    }


}
