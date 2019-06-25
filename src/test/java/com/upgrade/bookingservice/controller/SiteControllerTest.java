package com.upgrade.bookingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.bookingservice.controller.dto.AvailabilityRequest;
import com.upgrade.bookingservice.service.BookingService;
import com.upgrade.bookingservice.validator.AvailabilityValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@WebFluxTest(SiteController.class)
public class SiteControllerTest {

    @MockBean
    private AvailabilityValidator validator;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private WebTestClient webClient;

    @SuppressWarnings("unused")
    private JacksonTester<List<LocalDate>> localDateJacksonTester;

    @Before
    public void setUp() {

        JacksonTester.initFields(this, objectMapper);
        Hooks.onOperatorDebug();

        this.webClient = WebTestClient
                .bindToController(new SiteController(validator, bookingService))
                .httpMessageCodecs((configurer) -> {
                    CodecConfigurer.DefaultCodecs defaults = configurer.defaultCodecs();
                    defaults.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                    defaults.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                })
                .validator(validator)
                .configureClient()
                .build();
    }

    @Test
    public void shouldReturnAvailabilities() throws IOException {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusMonths(1);
        LocalDate firstAvailableDate = LocalDate.now().plusDays(1);
        LocalDate secondAvailableDate = LocalDate.now().plusDays(2);

        AvailabilityRequest availabilityRequest = AvailabilityRequest.builder()
                                                    .from(start)
                                                    .to(end)
                                                    .build();

        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.when(bookingService.findAvailabilitiesBetween(start, end)).thenReturn(Flux.just(firstAvailableDate, secondAvailableDate));

        this.webClient.get().uri("/site/availabilities", availabilityRequest)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(localDateJacksonTester.write(Arrays.asList(firstAvailableDate, secondAvailableDate)).getJson());
    }
}
