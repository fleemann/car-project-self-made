package com.example.carrental.exception;

/**
 * Wird geworfen, wenn ein Auto mit einer bestimmten ID nicht gefunden wird.
 * Erbt von {@link RuntimeException} (unchecked) und wird zentral im
 * {@link GlobalExceptionHandler} in eine 404-Antwort uebersetzt.
 */
public class CarNotFoundException extends RuntimeException {

    /**
     * @param id die nicht gefundene Auto-ID (wird in die Fehlermeldung aufgenommen)
     */
    public CarNotFoundException(int id) {
        super("Auto mit ID " + id + " wurde nicht gefunden.");
    }
}
