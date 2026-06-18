package com.example.carrental.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Zentralisierte Fehlerbehandlung fuer alle REST-Endpunkte.
 * Faengt Ausnahmen an einer Stelle ab und gibt einheitliche JSON-Fehler im
 * Format {@code {"error": "..."}} zurueck.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Unbekanntes Auto -> 404 Not Found. */
    @ExceptionHandler(CarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCarNotFound(CarNotFoundException exception) {
        logger.warn("Nicht gefunden: {}", exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    /** Bean-Validation-Fehler -> 400 Bad Request (alle Feldmeldungen zusammengefasst). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        logger.warn("Validierungsfehler: {}", message);
        return Map.of("error", message);
    }

    /** Ungueltige Anfrage (z.B. belegtes Auto, falsches Datum) -> 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException exception) {
        logger.warn("Ungueltige Anfrage: {}", exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    /** Auffangnetz fuer alle uebrigen Fehler -> 500 Internal Server Error. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception exception) {
        logger.error("Unerwarteter Fehler: {}", exception.getMessage(), exception);
        return Map.of("error", "Ein unerwarteter Fehler ist aufgetreten.");
    }
}
