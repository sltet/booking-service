package com.upgrade.bookingservice.controller;

import com.upgrade.bookingservice.controller.dto.ErrorDTO;
import com.upgrade.bookingservice.exception.NotFoundException;
import com.upgrade.bookingservice.exception.UnprocessableEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import java.util.Date;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFoundException(NotFoundException exception, ServerWebExchange exchange) {
        ErrorDTO error = createErrorDTO(exchange.getRequest(), exception, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorDTO> handleUnprocessableException(UnprocessableEntityException exception, ServerWebExchange exchange) {
        ErrorDTO error = createErrorDTO(exchange.getRequest(), exception, HttpStatus.UNPROCESSABLE_ENTITY);
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ErrorDTO createErrorDTO(ServerHttpRequest request, Exception exception, HttpStatus httpStatus) {
        return ErrorDTO.builder()
                .timestamp(new Date())
                .message(exception.getMessage())
                .path(request.getPath().value())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .build();
    }

}
