package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.exception.NotFoundException;
import com.upgrade.bookingservice.exception.UnprocessableEntityException;
import com.upgrade.bookingservice.model.CustomerReservation;
import com.upgrade.bookingservice.model.Status;
import com.upgrade.bookingservice.repository.CustomerReservationRepository;
import com.upgrade.bookingservice.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
public class CustomerReservationServiceTest {

    @Mock
    private CustomerReservation customerReservation;

    @Mock
    private BookingService bookingService;

    @Mock
    private CustomerReservationRepository customerReservationRepository;

    @InjectMocks
    private CustomerReservationService customerReservationService;


    @Before
    public void setUp() {
        Hooks.onOperatorDebug();
    }

    @Test
    public void shouldReturnCustomerReservationGivenReservationId() {
        Long reservationId = new Random().nextLong();

        Mockito.when(customerReservationRepository.findById(reservationId)).thenReturn(Optional.of(customerReservation));

        Mono<CustomerReservation> reservation = customerReservationService.findById(reservationId);

        StepVerifier.create(reservation)
                .expectNext(customerReservation)
                .verifyComplete();
    }

    @Test
    public void shouldReturnNotFoundExceptionGivenWrongReservationId() {
        Long reservationId = new Random().nextLong();

        Mockito.when(customerReservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        Mono<CustomerReservation> reservation = customerReservationService.findById(reservationId);

        StepVerifier.create(reservation)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void shouldReturnCreatedCustomerReservation() {
        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now(), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);

        String fullName = "john Doe";
        String email = "john@doe.com";

        CustomerReservation reservationRequest = CustomerReservation.builder()
                .fullName(fullName)
                .email(email)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .build();

        CustomerReservation expectedReservation = CustomerReservation.builder()
                .id(reservationId)
                .fullName(fullName)
                .email(email)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.COMPLETED)
                .build();

        Mockito.when(customerReservationRepository.save(reservationRequest)).thenReturn(expectedReservation);
        Mockito.when(bookingService.createBooking(expectedReservation)).thenReturn(Mono.just(expectedReservation));

        Mono<CustomerReservation> reservation = customerReservationService.create(reservationRequest);

        StepVerifier.create(reservation)
                .expectNext(expectedReservation)
                .verifyComplete();
    }

    @Test
    public void shouldUpdateCustomerReservationGivenReservationUpdate() {
        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now(), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);

        String fullName = "john Doe";
        String email = "john@doe.com";

        CustomerReservation updateReservationRequest = CustomerReservation.builder()
                .id(reservationId)
                .fullName(fullName)
                .email(email)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.COMPLETED)
                .build();

        CustomerReservation expectedReservation = CustomerReservation.builder()
                .id(reservationId)
                .fullName(fullName)
                .email(email)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.COMPLETED)
                .build();

        Mockito.when(customerReservationRepository.findById(updateReservationRequest.getId())).thenReturn(Optional.of(customerReservation));
        Mockito.when(customerReservation.getStatus()).thenReturn(Status.COMPLETED);
        Mockito.when(bookingService.updateBooking(customerReservation)).thenReturn(Mono.just(customerReservation));
        Mockito.when(customerReservationRepository.save(customerReservation)).thenReturn(expectedReservation);

        Mono<CustomerReservation> reservation = customerReservationService.update(updateReservationRequest);

        StepVerifier.create(reservation)
                .expectNext(expectedReservation)
                .verifyComplete();
    }

    @Test
    public void shouldThrowNotFoundExceptionOnCustomerReservationUpdateGivenCancelledReservation() {
        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now(), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);

        String fullName = "john Doe";
        String email = "john@doe.com";

        CustomerReservation updateReservationRequest = CustomerReservation.builder()
                .id(reservationId)
                .fullName(fullName)
                .email(email)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.COMPLETED)
                .build();

        Mockito.when(customerReservationRepository.findById(updateReservationRequest.getId())).thenReturn(Optional.empty());

        Mono<CustomerReservation> reservation = customerReservationService.update(updateReservationRequest);

        StepVerifier.create(reservation)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void shouldThrowUnprocessableEntityExceptionOnCustomerReservationUpdateGivenCancelledReservation() {
        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now(), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);

        String fullName = "john Doe";
        String email = "john@doe.com";

        CustomerReservation updateReservationRequest = CustomerReservation.builder()
                .id(reservationId)
                .fullName(fullName)
                .email(email)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.COMPLETED)
                .build();

        Mockito.when(customerReservationRepository.findById(updateReservationRequest.getId())).thenReturn(Optional.of(customerReservation));
        Mockito.when(customerReservation.getStatus()).thenReturn(Status.CANCELLED);

        Mono<CustomerReservation> reservation = customerReservationService.update(updateReservationRequest);

        StepVerifier.create(reservation)
                .expectError(UnprocessableEntityException.class)
                .verify();
    }

    @Test
    public void shouldReturnCancelledReservationGivenReservationId() {
        Long reservationId = new Random().nextLong();

        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now(), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);
        CustomerReservation expectedReservation = CustomerReservation.builder()
                .id(reservationId)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .status(Status.CANCELLED)
                .build();

        Mockito.when(customerReservationRepository.findById(reservationId)).thenReturn(Optional.of(customerReservation));
        Mockito.when(customerReservation.getStatus()).thenReturn(Status.COMPLETED);
        Mockito.when(bookingService.cancelBooking(customerReservation)).thenReturn(Mono.just(customerReservation));
        Mockito.when(customerReservationRepository.save(customerReservation)).thenReturn(expectedReservation);

        Mono<CustomerReservation> reservation = customerReservationService.cancel(reservationId);

        StepVerifier.create(reservation)
                .expectNext(expectedReservation)
                .verifyComplete();
    }

    @Test
    public void shouldThrowNotFoundExceptionOnCancelReservationGivenNotFoundReservation() {
        Long reservationId = new Random().nextLong();

        Mockito.when(customerReservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        Mono<CustomerReservation> reservation = customerReservationService.cancel(reservationId);

        StepVerifier.create(reservation)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void shouldThrowUnprocessableEntityExceptionOnCancelReservationGivenCanceledReservation() {
        Long reservationId = new Random().nextLong();

        Mockito.when(customerReservationRepository.findById(reservationId)).thenReturn(Optional.of(customerReservation));
        Mockito.when(customerReservation.getStatus()).thenReturn(Status.CANCELLED);

        Mono<CustomerReservation> reservation = customerReservationService.cancel(reservationId);

        StepVerifier.create(reservation)
                .expectError(UnprocessableEntityException.class)
                .verify();
    }
}
