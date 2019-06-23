package com.upgrade.bookingservice.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upgrade.bookingservice.serializer.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @ApiModelProperty(required = true, example = "2020-10-05")
    private LocalDate arrivalDate;

    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @ApiModelProperty(required = true, example = "2020-10-10")
    private LocalDate departureDate;
}
