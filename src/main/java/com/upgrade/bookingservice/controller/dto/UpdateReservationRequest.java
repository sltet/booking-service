package com.upgrade.bookingservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UpdateReservationRequest extends ReservationDTO {

    @Builder
    public UpdateReservationRequest(@NotNull LocalDate arrivalDate, @NotNull LocalDate departureDate) {
        super(arrivalDate, departureDate);
    }
}
