package com.example.carrental.repository;

import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Persistiert Autos als JSON-Datei auf dem Dateisystem.
 * Die Lese-/Schreibmethoden sind {@code synchronized} (thread-sicher) und
 * geben nach aussen Kopien heraus.
 */
@Repository
public class CarRepository {

    private static final Logger logger = LoggerFactory.getLogger(CarRepository.class);

    private final Path dataFile;
    private final ObjectMapper objectMapper;
    private final List<Car> cars;
    private final AtomicInteger idCounter;

    /**
     * Laedt die Autos beim Start und bestimmt den naechsten freien ID-Wert.
     *
     * @param dataFilePath Pfad zur JSON-Datei (Konfiguration, Default data/cars.json)
     * @param objectMapper Jackson-Mapper zum Lesen/Schreiben
     */
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
        logger.info("Repository gestartet: {} Autos aus '{}'", cars.size(), dataFile);
    }

    /** Gibt eine Kopie der Auto-Liste zurueck (schuetzt die interne Liste). */
    public synchronized List<Car> findAll() {
        return new ArrayList<>(cars);
    }

    /** Sucht ein Auto anhand seiner ID. */
    public synchronized Optional<Car> findById(int id) {
        return cars.stream()
                .filter(car -> car.getId() == id)
                .findFirst();
    }

    /** Vergibt die naechste ID, speichert das Auto und schreibt auf die Platte. */
    public synchronized Car save(Car car) {
        car.setId(idCounter.getAndIncrement());
        cars.add(car);
        persist();
        logger.debug("Auto gespeichert: id={}, {} {}", car.getId(), car.getBrand(), car.getModel());
        return car;
    }

    /** Aktualisiert ein vorhandenes Auto ueber {@link Car#updateFrom(Car)}. */
    public synchronized Optional<Car> update(int id, Car input) {
        Optional<Car> existing = findById(id);

        existing.ifPresent(car -> {
            car.updateFrom(input);
            persist();
            logger.debug("Auto aktualisiert: id={}", id);
        });

        return existing;
    }

    /** Entfernt ein Auto und speichert nur, wenn wirklich etwas entfernt wurde. */
    public synchronized boolean delete(int id) {
        boolean removed = cars.removeIf(car -> car.getId() == id);

        if (removed) {
            persist();
            logger.debug("Auto geloescht: id={}", id);
        }

        return removed;
    }

    /** Haengt eine Buchung an ein Auto an. */
    public synchronized Optional<Rental> addRental(int carId, Rental rental) {
        Optional<Car> existing = findById(carId);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        Car car = existing.get();
        car.getRentals().add(rental);

        persist();
        logger.debug("Miete hinzugefuegt fuer Auto id={}: {} bis {}", carId, rental.getStartDate(), rental.getEndDate());

        return Optional.of(rental);
    }

    /** Laedt die Autos aus der Datei oder legt beim ersten Start Seed-Daten an. */
    private List<Car> loadFromFile() {
        if (!Files.exists(dataFile)) {
            logger.info("Datendatei nicht gefunden, erstelle Initialdaten");
            List<Car> seed = createSeedData();
            persistList(seed);
            return seed;
        }

        try {
            Car[] loaded = objectMapper.readValue(dataFile.toFile(), Car[].class);
            logger.info("{} Autos aus Datei geladen", loaded.length);
            return new ArrayList<>(Arrays.asList(loaded));
        } catch (IOException exception) {
            logger.error("Fehler beim Lesen der Datendatei: {}", exception.getMessage());
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
                makeCar(1, "Volkswagen", "Golf", 49.0, "manual", 130, "gasoline", 2021, 5,
                        "https://loremflickr.com/600/400/volkswagen,golf,car/?lock=1"),
                makeCar(2, "Tesla", "Model 3", 89.0, "automatic", 283, "electro", 2023, 5,
                        "https://loremflickr.com/600/400/tesla,car/?lock=2"),
                makeCar(3, "Toyota", "Corolla", 59.0, "automatic", 140, "hybrid", 2022, 5,
                        "https://loremflickr.com/600/400/toyota,corolla,car/?lock=3"),
                makeCar(4, "BMW", "320i", 99.0, "automatic", 184, "gasoline", 2020, 5,
                        "https://loremflickr.com/600/400/bmw,car/?lock=4"),
                makeCar(5, "Renault", "Zoe", 45.0, "automatic", 108, "electro", 2021, 4,
                        "https://loremflickr.com/600/400/renault,zoe,car/?lock=5")
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
            int seats,
            String image
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
        car.setImage(image);

        return car;
    }
}
