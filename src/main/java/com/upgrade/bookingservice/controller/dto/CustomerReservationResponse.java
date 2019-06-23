package com.upgrade.bookingservice.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upgrade.bookingservice.serializer.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("CustomerReservation")
public class CustomerReservationResponse {

    private Long id;
    private String fullName;
    private String email;
    private String status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime arrivalDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime departureDate;

}
