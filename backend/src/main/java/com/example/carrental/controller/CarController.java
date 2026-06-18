package com.example.carrental.controller;

import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.example.carrental.model.SearchParams;
import com.example.carrental.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller fuer alle Auto-Endpunkte.
 * Basispfad: {@code /api/v1/car}. Der Controller enthaelt keine Logik,
 * sondern delegiert an den {@link CarService}.
 */
@RestController
@RequestMapping("/api/v1/car")
@Tag(name = "Autos", description = "Autovermietung REST API")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    /**
     * Sucht und filtert Autos. Die Query-Parameter werden via
     * {@code @ModelAttribute} in ein {@link SearchParams}-Objekt gebunden.
     */
    @GetMapping
    @Operation(summary = "Autos suchen",
            description = "Filtert nach Marke, Modell, Getriebe, Motor, Preis, PS, Baujahr, Sitze und Verfuegbarkeit")
    public List<Car> search(@ModelAttribute SearchParams params) {
        logger.debug("GET /api/v1/car");
        return service.search(params);
    }

    /** Liefert ein einzelnes Auto anhand seiner ID. */
    @GetMapping("/{id}")
    @Operation(summary = "Auto nach ID abrufen")
    public Car getById(@PathVariable @Parameter(description = "Auto-ID") int id) {
        logger.debug("GET /api/v1/car/{}", id);
        return service.findById(id);
    }

    /** Legt ein neues, validiertes Auto an und antwortet mit 201 Created. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neues Auto erstellen")
    public Car create(@Valid @RequestBody Car car) {
        logger.debug("POST /api/v1/car");
        return service.create(car);
    }

    /** Aktualisiert ein bestehendes Auto. */
    @PutMapping("/{id}")
    @Operation(summary = "Auto aktualisieren")
    public Car update(@PathVariable int id, @Valid @RequestBody Car car) {
        logger.debug("PUT /api/v1/car/{}", id);
        return service.update(id, car);
    }

    /** Loescht ein Auto und antwortet mit 204 No Content. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Auto loeschen")
    public void delete(@PathVariable int id) {
        logger.debug("DELETE /api/v1/car/{}", id);
        service.delete(id);
    }

    /** Bucht ein Auto fuer einen Zeitraum (YYYY-MM-DD) nach Verfuegbarkeitspruefung. */
    @PostMapping("/{id}/rentals")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Auto mieten", description = "Bucht ein Auto fuer einen Zeitraum (YYYY-MM-DD)")
    public Rental rent(@PathVariable int id, @Valid @RequestBody Rental rental) {
        logger.debug("POST /api/v1/car/{}/rentals", id);
        return service.rent(id, rental);
    }
}
