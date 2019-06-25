package com.upgrade.bookingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    private LocalDateTime arrivalDate;
    private LocalDateTime departureDate;
    private Long reservationId;
}
