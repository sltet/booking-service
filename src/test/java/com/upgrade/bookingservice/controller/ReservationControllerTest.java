package com.upgrade.bookingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.upgrade.bookingservice.controller.dto.CustomerReservationResponse;
import com.upgrade.bookingservice.controller.dto.ReservationRequest;
import com.upgrade.bookingservice.controller.dto.UpdateReservationRequest;
import com.upgrade.bookingservice.converter.ReservationConverter;
import com.upgrade.bookingservice.model.CustomerReservation;
import com.upgrade.bookingservice.model.Status;
import com.upgrade.bookingservice.serializer.LocalDateTimeSerializer;
import com.upgrade.bookingservice.service.ReservationService;
import com.upgrade.bookingservice.util.Constants;
import com.upgrade.bookingservice.validator.ReservationValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@WebFluxTest(ReservationController.class)
public class ReservationControllerTest {

    @Mock
    private CustomerReservation customerReservation;

    @MockBean
    private ReservationValidator reservationValidator;

    @MockBean
    private ReservationConverter reservationConverter;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webClient;

    @SuppressWarnings("unused")
    private JacksonTester<CustomerReservationResponse> reservationJacksonTester;

    @Before
    public void setUp() {

        objectMapper.registerModule(new SimpleModule().addSerializer(LocalDateTime.class, new LocalDateTimeSerializer()));
        JacksonTester.initFields(this, objectMapper);
        Hooks.onOperatorDebug();

        this.webClient = WebTestClient
                .bindToController(new ReservationController(reservationValidator, reservationService, reservationConverter))
                .httpMessageCodecs((configurer) -> {
                    CodecConfigurer.DefaultCodecs defaults = configurer.defaultCodecs();
                    defaults.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, new MimeType[0]));
                    defaults.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, new MimeType[0]));
                })
                .validator(reservationValidator)
                .configureClient()
                .build();
    }

    @Test
    public void shouldReturnReservationGivenReservationId() throws IOException {

        Long reservationId = new Random().nextLong();

        CustomerReservationResponse expectedReservation = CustomerReservationResponse.builder()
                .id(reservationId)
                .arrivalDate(LocalDateTime.of(LocalDate.now(), Constants.DEFAULT_CHECK_IN_TIME))
                .departureDate(LocalDateTime.of(LocalDate.now().plusMonths(1), Constants.DEFAULT_CHECK_IN_TIME))
                .status(Status.COMPLETED.name())
                .build();

        Mockito.when(reservationValidator.supports(any())).thenReturn(true);
        Mockito.when(reservationService.findById(reservationId)).thenReturn(Mono.just(customerReservation));
        Mockito.when(reservationConverter.convert(customerReservation)).thenReturn(expectedReservation);

        this.webClient.get().uri("/reservations/{reservationId}", reservationId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(reservationJacksonTester.write(expectedReservation).getJson());
    }

    @Test
    public void shouldReturnCreatedReservationGivenReservationRequest() throws IOException {

        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(2), Constants.DEFAULT_CHECK_IN_TIME);

        ReservationRequest reservationRequest = ReservationRequest.builder()
                .email("john@doe.com")
                .fullName("John Doe")
                .arrivalDate(arrivalDate.toLocalDate())
                .departureDate(departureDate.toLocalDate())
                .build();

        CustomerReservationResponse expectedReservation = CustomerReservationResponse.builder()
                .id(reservationId)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.COMPLETED.name())
                .build();

        Mockito.when(reservationValidator.supports(any())).thenReturn(true);
        Mockito.when(reservationConverter.create(reservationRequest)).thenReturn(customerReservation);
        Mockito.when(reservationService.create(customerReservation)).thenReturn(Mono.just(customerReservation));
        Mockito.when(reservationConverter.convert(customerReservation)).thenReturn(expectedReservation);

        this.webClient.post().uri("/reservations")
                .body(BodyInserters.fromObject(reservationRequest))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().json(reservationJacksonTester.write(expectedReservation).getJson());
    }

    @Test
    public void shouldReturnUpdatedReservationGivenUpdateReservationRequest() throws IOException {

        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(2), Constants.DEFAULT_CHECK_IN_TIME);

        UpdateReservationRequest reservationRequest = UpdateReservationRequest.builder()
                .arrivalDate(arrivalDate.toLocalDate())
                .departureDate(departureDate.toLocalDate())
                .build();

        CustomerReservationResponse expectedReservation = CustomerReservationResponse.builder()
                .id(reservationId)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.COMPLETED.name())
                .build();

        Mockito.when(reservationValidator.supports(any())).thenReturn(true);
        Mockito.when(reservationConverter.update(reservationId, reservationRequest)).thenReturn(customerReservation);
        Mockito.when(reservationService.update(customerReservation)).thenReturn(Mono.just(customerReservation));
        Mockito.when(reservationConverter.convert(customerReservation)).thenReturn(expectedReservation);

        this.webClient.put().uri("/reservations/{reservationId}", reservationId)
                .body(BodyInserters.fromObject(reservationRequest))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(reservationJacksonTester.write(expectedReservation).getJson());
    }

    @Test
    public void shouldReturnCancelledReservationGivenReservationId() throws IOException {

        Long reservationId = new Random().nextLong();

        CustomerReservationResponse expectedReservation = CustomerReservationResponse.builder()
                .id(reservationId)
                .arrivalDate(LocalDateTime.of(LocalDate.now(), Constants.DEFAULT_CHECK_IN_TIME))
                .departureDate(LocalDateTime.of(LocalDate.now().plusMonths(1), Constants.DEFAULT_CHECK_IN_TIME))
                .status(Status.CANCELLED.name())
                .build();

        Mockito.when(reservationValidator.supports(any())).thenReturn(true);
        Mockito.when(reservationService.cancel(reservationId)).thenReturn(Mono.just(customerReservation));
        Mockito.when(reservationConverter.convert(customerReservation)).thenReturn(expectedReservation);

        this.webClient.get().uri("/reservations/{reservationId}/cancel", reservationId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(reservationJacksonTester.write(expectedReservation).getJson());
    }
}
