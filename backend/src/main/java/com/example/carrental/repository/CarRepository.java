package com.example.carrental.repository;

import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class CarRepository {

    private final Path dataFile;
    private final ObjectMapper objectMapper;
    private final List<Car> cars;
    private final AtomicInteger idCounter;

    public CarRepository(
            @Value("${app.data.file:data/cars.json}") String dataFilePath,
            ObjectMapper objectMapper
    ) {
        this.dataFile = Path.of(dataFilePath);
        this.objectMapper = objectMapper;
        this.cars = new ArrayList<>(loadFromFile());

        int maxId = cars.stream()
                .mapToInt(car -> car.getId() != null ? car.getId() : 0)
                .max()
                .orElse(0);

        this.idCounter = new AtomicInteger(maxId + 1);
    }

    public synchronized List<Car> findAll() {
        return new ArrayList<>(cars);
    }

    public synchronized Optional<Car> findById(int id) {
        return cars.stream()
                .filter(car -> car.getId() == id)
                .findFirst();
    }

    public synchronized Car save(Car car) {
        car.setId(idCounter.getAndIncrement());
        cars.add(car);
        persist();
        return car;
    }

    public synchronized Optional<Car> update(int id, Car input) {
        Optional<Car> existing = findById(id);

        if (existing.isPresent()) {
            Car car = existing.get();

            car.setBrand(input.getBrand());
            car.setModel(input.getModel());
            car.setPrice(input.getPrice());
            car.setGear(input.getGear());
            car.setPs(input.getPs());
            car.setEngine(input.getEngine());
            car.setYear(input.getYear());
            car.setSeats(input.getSeats());

            persist();
        }

        return existing;
    }

    public synchronized boolean delete(int id) {
        boolean removed = cars.removeIf(car -> car.getId() == id);

        if (removed) {
            persist();
        }

        return removed;
    }

    public synchronized Optional<Rental> addRental(int carId, Rental rental) {
        Optional<Car> existing = findById(carId);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        Car car = existing.get();
        car.getRentals().add(rental);

        persist();

        return Optional.of(rental);
    }

    private List<Car> loadFromFile() {
        if (!Files.exists(dataFile)) {
            List<Car> seed = createSeedData();
            persistList(seed);
            return seed;
        }

        try {
            Car[] loaded = objectMapper.readValue(dataFile.toFile(), Car[].class);
            return new ArrayList<>(Arrays.asList(loaded));
        } catch (IOException exception) {
            return createSeedData();
        }
    }

    private void persist() {
        persistList(cars);
    }

    private void persistList(List<Car> list) {
        try {
            Path parent = dataFile.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(dataFile.toFile(), list);
        } catch (IOException exception) {
            throw new IllegalStateException("Autos konnten nicht gespeichert werden.", exception);
        }
    }

    private List<Car> createSeedData() {
        return new ArrayList<>(List.of(
                makeCar(1, "Volkswagen", "Golf", 49.0, "manual", 130, "gasoline", 2021, 5),
                makeCar(2, "Tesla", "Model 3", 89.0, "automatic", 283, "electro", 2023, 5),
                makeCar(3, "Toyota", "Corolla", 59.0, "automatic", 140, "hybrid", 2022, 5),
                makeCar(4, "BMW", "320i", 99.0, "automatic", 184, "gasoline", 2020, 5),
                makeCar(5, "Renault", "Zoe", 45.0, "automatic", 108, "electro", 2021, 4)
        ));
    }

    private Car makeCar(
            int id,
            String brand,
            String model,
            double price,
            String gear,
            int ps,
            String engine,
            int year,
            int seats
    ) {
        Car car = new Car();

        car.setId(id);
        car.setBrand(brand);
        car.setModel(model);
        car.setPrice(price);
        car.setGear(gear);
        car.setPs(ps);
        car.setEngine(engine);
        car.setYear(year);
        car.setSeats(seats);

        return car;
    }
}