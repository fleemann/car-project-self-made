package com.example.carrental.controller;

import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.example.carrental.model.SearchParams;
import com.example.carrental.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/car")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping
    public List<Car> search(SearchParams params) {
        return service.search(params);
    }

    @GetMapping("/{id}")
    public Car getById(@PathVariable int id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car create(@Valid @RequestBody Car car) {
        return service.create(car);
    }

    @PutMapping("/{id}")
    public Car update(@PathVariable int id, @Valid @RequestBody Car car) {
        return service.update(id, car);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @PostMapping("/{id}/rentals")
    @ResponseStatus(HttpStatus.CREATED)
    public Rental rent(@PathVariable int id, @Valid @RequestBody Rental rental) {
        return service.rent(id, rental);
    }
}