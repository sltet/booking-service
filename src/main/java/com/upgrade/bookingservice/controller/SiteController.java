package com.upgrade.bookingservice.controller;

import com.upgrade.bookingservice.controller.dto.AvailabilityRequest;
import com.upgrade.bookingservice.service.ReservationService;
import com.upgrade.bookingservice.validator.AvailabilityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/site")
@RequiredArgsConstructor
public class SiteController implements SiteApi {

    private final AvailabilityValidator availabilityValidator;
    private final ReservationService reservationService;

    @Override
    @GetMapping("/availabilities")
    public Mono<ResponseEntity<List<LocalDate>>> getAvailabilities(@Valid AvailabilityRequest availabilityRequest) {
        return reservationService.findAvailabilitiesBetween(availabilityRequest.getFrom(), availabilityRequest.getTo())
                .collectList()
                .map(ResponseEntity::ok);
    }

    @InitBinder(value = {"availabilityRequest"})
    public void init(WebDataBinder binder) {
        binder.addValidators(availabilityValidator);
    }

}
