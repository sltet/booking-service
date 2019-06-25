package com.upgrade.bookingservice.service;

import com.upgrade.bookingservice.model.Booking;
import com.upgrade.bookingservice.model.CustomerReservation;
import com.upgrade.bookingservice.repository.BookingRepository;
import com.upgrade.bookingservice.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CustomerBookingServiceTest {

    @Mock
    private Booking booking;

    @Mock
    private CustomerReservation customerReservation;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CustomerBookingService customerBookingService;


    @Before
    public void setUp() {
        Hooks.onOperatorDebug();
    }

    @Test
    public void shouldReturnCustomerReservationOnCreateBooking() {
        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(2), Constants.DEFAULT_CHECK_IN_TIME);


        Mockito.when(customerReservation.getId()).thenReturn(reservationId);
        Mockito.when(customerReservation.getArrivalDate()).thenReturn(arrivalDate);
        Mockito.when(customerReservation.getDepartureDate()).thenReturn(departureDate);
        Mockito.when(bookingRepository.isRangeAvailable(arrivalDate, departureDate)).thenReturn(true);
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        Mono<CustomerReservation> reservation = customerBookingService.createBooking(customerReservation);

        StepVerifier.create(reservation)
                .expectNext(customerReservation)
                .verifyComplete();
    }

    @Test
    public void shouldReturnCustomerReservationOnUpdateBooking() {
        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(2), Constants.DEFAULT_CHECK_IN_TIME);

        Mockito.when(customerReservation.getId()).thenReturn(reservationId);
        Mockito.when(customerReservation.getArrivalDate()).thenReturn(arrivalDate);
        Mockito.when(customerReservation.getDepartureDate()).thenReturn(departureDate);
        Mockito.when(bookingRepository.update(any())).thenReturn(booking);

        Mono<CustomerReservation> reservation = customerBookingService.updateBooking(customerReservation);

        StepVerifier.create(reservation)
                .expectNext(customerReservation)
                .verifyComplete();
    }

    @Test
    public void shouldReturnCustomerReservationOnCancelBooking() {
        Long reservationId = new Random().nextLong();
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(2), Constants.DEFAULT_CHECK_IN_TIME);

        Mockito.when(customerReservation.getId()).thenReturn(reservationId);
        Mockito.when(customerReservation.getArrivalDate()).thenReturn(arrivalDate);
        Mockito.when(customerReservation.getDepartureDate()).thenReturn(departureDate);

        Mono<CustomerReservation> reservation = customerBookingService.cancelBooking(customerReservation);

        StepVerifier.create(reservation)
                .expectNext(customerReservation)
                .verifyComplete();
    }

    @Test
    public void shouldReturnAvailabilitiesGivenArrivalDateAndDepartureDate() {
        LocalDateTime arrivalDate = LocalDateTime.of(LocalDate.now().plusDays(1), Constants.DEFAULT_CHECK_IN_TIME);
        LocalDateTime departureDate = LocalDateTime.of(LocalDate.now().plusDays(2), Constants.DEFAULT_CHECK_IN_TIME);

        Mockito.when(bookingRepository.findAvailabilitiesBetween(arrivalDate, departureDate)).thenReturn(Flux.empty());

        Flux<LocalDate> availabilities = customerBookingService.findAvailabilitiesBetween(arrivalDate.toLocalDate(), departureDate.toLocalDate());

        StepVerifier.create(availabilities)
                .expectNextCount(0)
                .verifyComplete();
    }
}
