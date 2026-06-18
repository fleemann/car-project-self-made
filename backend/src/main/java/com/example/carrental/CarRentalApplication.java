package com.example.carrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der Anwendung.
 * {@code @SpringBootApplication} aktiviert die Auto-Konfiguration und durchsucht
 * das Paket nach Komponenten (Controller, Service, Repository).
 */
@SpringBootApplication
public class CarRentalApplication {

    /**
     * Startet den eingebetteten Webserver und baut den Spring-Kontext auf.
     *
     * @param args Kommandozeilen-Argumente
     */
    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
    }
}
