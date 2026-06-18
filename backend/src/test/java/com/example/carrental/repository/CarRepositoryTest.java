package com.example.carrental.repository;

import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CarRepositoryTest {

    @TempDir
    Path tempDir;

    private CarRepository repository;

    @BeforeEach
    void setUp() {
        Path dataFile = tempDir.resolve("cars.json");
        repository = new CarRepository(dataFile.toString(), new ObjectMapper());
    }

    @Test
    void findAll_returnsInitialSeedCars() {
        List<Car> cars = repository.findAll();

        assertThat(cars).hasSize(5);
        assertThat(cars)
                .extracting(Car::getBrand)
                .contains("Volkswagen", "Tesla", "Toyota", "BMW", "Renault");
    }

    @Test
    void findById_returnsCarWhenIdExists() {
        Optional<Car> result = repository.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getBrand()).isEqualTo("Volkswagen");
        assertThat(result.get().getModel()).isEqualTo("Golf");
    }

    @Test
    void findById_returnsEmptyWhenIdDoesNotExist() {
        Optional<Car> result = repository.findById(999);

        assertThat(result).isEmpty();
    }

    @Test
    void save_assignsNewIdStoresCarAndPersistsIt() {
        Car audi = makeCar("Audi", "A4", 85.0, "automatic", 190, "gasoline", 2022, 5);

        Car saved = repository.save(audi);

        assertThat(saved.getId()).isEqualTo(6);
        assertThat(saved.getBrand()).isEqualTo("Audi");

        List<Car> cars = repository.findAll();
        assertThat(cars).hasSize(6);
        assertThat(cars)
                .extracting(Car::getBrand)
                .contains("Audi");

        CarRepository reloadedRepository = new CarRepository(
                tempDir.resolve("cars.json").toString(),
                new ObjectMapper()
        );

        assertThat(reloadedRepository.findAll()).hasSize(6);
        assertThat(reloadedRepository.findById(6)).isPresent();
        assertThat(reloadedRepository.findById(6).get().getBrand()).isEqualTo("Audi");
    }

    @Test
    void update_changesExistingCarFieldsButKeepsId() {
        Car input = makeCar("Audi", "A6", 95.0, "automatic", 245, "gasoline", 2023, 5);

        Optional<Car> updated = repository.update(1, input);

        assertThat(updated).isPresent();
        assertThat(updated.get().getId()).isEqualTo(1);
        assertThat(updated.get().getBrand()).isEqualTo("Audi");
        assertThat(updated.get().getModel()).isEqualTo("A6");
        assertThat(updated.get().getPrice()).isEqualTo(95.0);
        assertThat(updated.get().getPs()).isEqualTo(245);
    }

    @Test
    void update_returnsEmptyWhenCarDoesNotExist() {
        Car input = makeCar("Audi", "A6", 95.0, "automatic", 245, "gasoline", 2023, 5);

        Optional<Car> updated = repository.update(999, input);

        assertThat(updated).isEmpty();
    }

    @Test
    void delete_removesExistingCar() {
        boolean deleted = repository.delete(1);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(1)).isEmpty();
        assertThat(repository.findAll()).hasSize(4);
    }

    @Test
    void delete_returnsFalseWhenCarDoesNotExist() {
        boolean deleted = repository.delete(999);

        assertThat(deleted).isFalse();
        assertThat(repository.findAll()).hasSize(5);
    }

    @Test
    void addRental_addsRentalToExistingCar() {
        Rental rental = new Rental(null, "2026-07-01", "2026-07-07");

        Optional<Rental> result = repository.addRental(1, rental);

        assertThat(result).isPresent();
        assertThat(result.get().getStartDate()).isEqualTo("2026-07-01");

        Car car = repository.findById(1).orElseThrow();
        assertThat(car.getRentals()).hasSize(1);
        assertThat(car.getRentals().get(0).getEndDate()).isEqualTo("2026-07-07");
    }

    @Test
    void addRental_returnsEmptyWhenCarDoesNotExist() {
        Rental rental = new Rental(null, "2026-07-01", "2026-07-07");

        Optional<Rental> result = repository.addRental(999, rental);

        assertThat(result).isEmpty();
    }

    private Car makeCar(
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