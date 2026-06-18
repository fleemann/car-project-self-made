package com.example.carrental.service;

import com.example.carrental.exception.CarNotFoundException;
import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.example.carrental.model.SearchParams;
import com.example.carrental.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    public List<Car> search(SearchParams params) {
        List<Car> result = repository.findAll().stream()
                .filter(car -> matchesText(car.getBrand(), params.getBrand()))
                .filter(car -> matchesText(car.getModel(), params.getModel()))
                .filter(car -> matchesExact(car.getGear(), params.getGear()))
                .filter(car -> matchesExact(car.getEngine(), params.getEngine()))
                .filter(car -> params.getMinPrice() == null || car.getPrice() >= params.getMinPrice())
                .filter(car -> params.getMaxPrice() == null || car.getPrice() <= params.getMaxPrice())
                .filter(car -> params.getPsMin() == null || car.getPs() >= params.getPsMin())
                .filter(car -> params.getPsMax() == null || car.getPs() <= params.getPsMax())
                .filter(car -> params.getYearMin() == null || car.getYear() >= params.getYearMin())
                .filter(car -> params.getSeats() == null || car.getSeats() >= params.getSeats())
                .filter(car -> isAvailable(car, params.getStartDate(), params.getEndDate()))
                .collect(Collectors.toList());

        if ("priceDesc".equals(params.getSort())) {
            result.sort(Comparator.comparingDouble(Car::getPrice).reversed());
        } else {
            result.sort(Comparator.comparingDouble(Car::getPrice));
        }

        return result;
    }

    public Car findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    public Car create(Car car) {
        return repository.save(car);
    }

    public Car update(int id, Car input) {
        return repository.update(id, input)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    public void delete(int id) {
        boolean deleted = repository.delete(id);

        if (!deleted) {
            throw new CarNotFoundException(id);
        }
    }

    public Rental rent(int carId, Rental rental) {
        validateDates(rental.getStartDate(), rental.getEndDate());

        Car car = findById(carId);

        LocalDate start = LocalDate.parse(rental.getStartDate());
        LocalDate end = LocalDate.parse(rental.getEndDate());

        boolean conflict = car.getRentals().stream()
                .anyMatch(existingRental -> existingRental.overlaps(start, end));

        if (conflict) {
            throw new IllegalArgumentException("Das Auto ist in diesem Zeitraum nicht verfügbar.");
        }

        rental.setCarId(carId);

        return repository.addRental(carId, rental)
                .orElseThrow(() -> new CarNotFoundException(carId));
    }

    private boolean isAvailable(Car car, String startDate, String endDate) {
        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
            return true;
        }

        validateDates(startDate, endDate);

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return car.getRentals().stream()
                .noneMatch(rental -> rental.overlaps(start, end));
    }

    private void validateDates(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (end.isBefore(start)) {
                throw new IllegalArgumentException("Enddatum muss nach dem Startdatum liegen.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Datum muss im Format YYYY-MM-DD angegeben werden.");
        }
    }

    private boolean matchesText(String value, String query) {
        return query == null
                || query.isBlank()
                || value.toLowerCase().contains(query.toLowerCase());
    }

    private boolean matchesExact(String value, String query) {
        return query == null
                || query.isBlank()
                || value.equalsIgnoreCase(query);
    }
}