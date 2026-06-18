package com.example.carrental.exception;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(int id) {
        super("Auto mit ID " + id + " wurde nicht gefunden.");
    }
}