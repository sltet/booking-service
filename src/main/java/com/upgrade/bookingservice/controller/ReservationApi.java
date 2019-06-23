package com.upgrade.bookingservice.controller;

import com.upgrade.bookingservice.controller.dto.CustomerReservationResponse;
import com.upgrade.bookingservice.controller.dto.ReservationRequest;
import com.upgrade.bookingservice.controller.dto.UpdateReservationRequest;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Api(value = "Reservations", description = "Reservations API", tags = {"reservation"})
public interface ReservationApi {


    @ApiOperation(
            value = "Find a reservation",
            nickname = "find reservation by id",
            response = CustomerReservationResponse.class,
            tags={"reservation"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok", response = CustomerReservationResponse.class),
            @ApiResponse(code = 400, message = "bad request") })
    Mono<ResponseEntity<CustomerReservationResponse>> findReservationById(@ApiParam(value = "reservation id",required=true) @PathVariable("reservationId") Long reservationId);


    @ApiOperation(
            value = "Create a reservation",
            nickname = "create a reservation",
            response = CustomerReservationResponse.class,
            tags={"reservation"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "created", response = CustomerReservationResponse.class),
            @ApiResponse(code = 400, message = "bad request") })
    Mono<ResponseEntity<CustomerReservationResponse>> createReservation(@ApiParam(name = "reservationRequest", value = "reservation request",required=true) ReservationRequest reservationRequest);

    @ApiOperation(
            value = "Update a reservation",
            nickname = "update a reservation",
            response = CustomerReservationResponse.class,
            tags={"reservation"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok", response = CustomerReservationResponse.class),
            @ApiResponse(code = 400, message = "bad request") })
    Mono<ResponseEntity<CustomerReservationResponse>> updateReservation(@ApiParam(value = "reservation id",required=true) @PathVariable("reservationId") Long reservationId, @ApiParam(value = "update reservation request",required=true)@Valid @RequestBody UpdateReservationRequest updateReservationRequest);

    @ApiOperation(
            value = "Cancel a reservation",
            nickname = "cancel a reservation",
            response = CustomerReservationResponse.class,
            tags={"reservation"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok", response = CustomerReservationResponse.class),
            @ApiResponse(code = 400, message = "bad request") })
    Mono<ResponseEntity<CustomerReservationResponse>> cancelReservation(@ApiParam(value = "reservation id",required=true) Long reservationId);
}
