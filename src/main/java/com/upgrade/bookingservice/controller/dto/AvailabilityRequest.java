package com.upgrade.bookingservice.controller.dto;

import com.upgrade.bookingservice.util.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequest {

    @ApiModelProperty(value = "start date", example = "2019-07-05")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from = LocalDate.now();

    @ApiModelProperty(value = "end date", example = "2019-07-15")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to = LocalDate.now().plusMonths(Constants.MAXIMAL_RESERVATION_REQUEST_MONTHS);

}
