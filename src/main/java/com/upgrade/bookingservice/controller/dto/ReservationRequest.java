package com.upgrade.bookingservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ReservationRequest extends ReservationDTO {

    @NotNull(message="Full name cannot be missing or empty")
    @Size(min=4, message="Full name must not be less than 2 characters")
    @ApiModelProperty(example = "John Doe")
    private String fullName;

    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    @ApiModelProperty(example = "john@doe.com")
    private String email;

}
