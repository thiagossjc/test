package org.inditex.prices.infrastructure.adapter.in.rest;

import org.inditex.prices.domain.execption.PriceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for REST controllers.
 * <p>
 * This class intercepts exceptions thrown by controller methods and maps them to
 * appropriate HTTP responses with standardized error payloads.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type {@link PriceNotFoundException}.
     *
     * @param ex the {@link PriceNotFoundException} thrown.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} with error details and HTTP status 404 (Not Found).
     */
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFound(PriceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("PRICE_NOT_FOUND", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex the general {@link Exception} thrown.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} with generic error details and HTTP status 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Error response payload used for returning error details in REST responses.
 *
 * @param code    the error code representing the error type.
 * @param message a human-readable error message.
 */
record ErrorResponse(String code, String message) {}