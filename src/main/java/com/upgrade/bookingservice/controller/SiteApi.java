package com.upgrade.bookingservice.controller;

import com.upgrade.bookingservice.controller.dto.AvailabilityRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Api(value = "Site", description = "Site API", tags = {"site"})
public interface SiteApi {

    @ApiOperation(
            value = "Find site availabilities",
            nickname = "find site availabilities",
            response = List.class,
            tags={"site"}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok", response = List.class),
            @ApiResponse(code = 400, message = "bad request") })
    Mono<ResponseEntity<List<LocalDate>>> getAvailabilities(@Valid AvailabilityRequest availabilityRequest);
}
