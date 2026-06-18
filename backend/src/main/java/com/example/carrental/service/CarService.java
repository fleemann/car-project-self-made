package com.example.carrental.service;

import com.example.carrental.exception.CarNotFoundException;
import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.example.carrental.model.SearchParams;
import com.example.carrental.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Geschaeftslogik der Autovermietung: Filterung, Sortierung,
 * Verfuegbarkeitspruefung und Buchung. Kennt weder HTTP- noch Datei-Details.
 */
@Service
public class CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);

    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    /**
     * Gibt eine gefilterte und nach Preis sortierte Liste von Autos zurueck.
     * Jeder Filter laesst alles durch, wenn der zugehoerige Parameter nicht
     * gesetzt ist.
     */
    public List<Car> search(SearchParams params) {
        logger.info("Suche mit brand={}, gear={}, maxPrice={}",
                params.getBrand(), params.getGear(), params.getMaxPrice());

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

        logger.info("Suche ergab {} Autos", result.size());
        return result;
    }

    /** Liefert ein Auto oder wirft {@link CarNotFoundException}. */
    public Car findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    /** Legt ein neues Auto an. */
    public Car create(Car car) {
        Car saved = repository.save(car);
        logger.info("Auto erstellt: id={}, {} {}", saved.getId(), saved.getBrand(), saved.getModel());
        return saved;
    }

    /** Aktualisiert ein bestehendes Auto oder wirft {@link CarNotFoundException}. */
    public Car update(int id, Car input) {
        logger.info("Auto aktualisieren id={}", id);
        return repository.update(id, input)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    /** Loescht ein Auto oder wirft {@link CarNotFoundException}. */
    public void delete(int id) {
        logger.info("Auto loeschen id={}", id);
        boolean deleted = repository.delete(id);

        if (!deleted) {
            throw new CarNotFoundException(id);
        }
    }

    /**
     * Bucht ein Auto nach Pruefung von Datum und Verfuegbarkeit.
     *
     * @throws IllegalArgumentException bei ungueltigem Datum oder Belegung
     * @throws CarNotFoundException     wenn das Auto nicht existiert
     */
    public Rental rent(int carId, Rental rental) {
        logger.info("Miete fuer Auto id={}: {} bis {}", carId, rental.getStartDate(), rental.getEndDate());

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

    /** Prueft, ob ein Auto im angegebenen Zeitraum frei ist (Gegenstueck zu rent: noneMatch). */
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

    /**
     * Validiert ein Datumspaar: korrektes Format, Start nicht in der
     * Vergangenheit und Ende nicht vor dem Start.
     *
     * @throws IllegalArgumentException wenn eine Regel verletzt ist
     */
    private void validateDates(String startDate, String endDate) {
        LocalDate start;
        LocalDate end;

        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Datum muss im Format YYYY-MM-DD angegeben werden.");
        }

        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Startdatum darf nicht in der Vergangenheit liegen.");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Enddatum muss nach dem Startdatum liegen.");
        }
    }

    /** Teilstring-Suche, gross-/klein-unabhaengig; leerer Filter passt immer. */
    private boolean matchesText(String value, String query) {
        return query == null
                || query.isBlank()
                || value.toLowerCase().contains(query.toLowerCase());
    }

    /** Exakter Vergleich, gross-/klein-unabhaengig; leerer Filter passt immer. */
    private boolean matchesExact(String value, String query) {
        return query == null
                || query.isBlank()
                || value.equalsIgnoreCase(query);
    }
}
