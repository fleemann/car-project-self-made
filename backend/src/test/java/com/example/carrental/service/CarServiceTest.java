package com.example.carrental.service;

import com.example.carrental.exception.CarNotFoundException;
import com.example.carrental.model.Car;
import com.example.carrental.model.Rental;
import com.example.carrental.model.SearchParams;
import com.example.carrental.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository repository;

    @InjectMocks
    private CarService service;

    private Car vw;
    private Car bmw;

    @BeforeEach
    void setUp() {
        vw = makeCar(1, "Volkswagen", "Golf", 49.0, "manual", 130, "gasoline", 2021, 5);
        bmw = makeCar(2, "BMW", "320i", 99.0, "automatic", 184, "gasoline", 2020, 5);
    }

    @Test
    void findById_returnsCarWhenFound() {
        when(repository.findById(1)).thenReturn(Optional.of(vw));

        Car result = service.findById(1);

        assertThat(result.getBrand()).isEqualTo("Volkswagen");
        assertThat(result.getModel()).isEqualTo("Golf");
    }

    @Test
    void findById_throwsCarNotFoundExceptionWhenMissing() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void search_filtersByBrand() {
        when(repository.findAll()).thenReturn(List.of(vw, bmw));

        SearchParams params = new SearchParams();
        params.setBrand("volks");

        List<Car> result = service.search(params);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Volkswagen");
    }

    @Test
    void search_filtersByMaxPrice() {
        when(repository.findAll()).thenReturn(List.of(vw, bmw));

        SearchParams params = new SearchParams();
        params.setMaxPrice(60.0);

        List<Car> result = service.search(params);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModel()).isEqualTo("Golf");
    }

    @Test
    void search_filtersByGear() {
        when(repository.findAll()).thenReturn(List.of(vw, bmw));

        SearchParams params = new SearchParams();
        params.setGear("automatic");

        List<Car> result = service.search(params);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("BMW");
    }

    @Test
    void search_sortsByPriceAscByDefault() {
        when(repository.findAll()).thenReturn(List.of(bmw, vw));

        SearchParams params = new SearchParams();

        List<Car> result = service.search(params);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBrand()).isEqualTo("Volkswagen");
        assertThat(result.get(1).getBrand()).isEqualTo("BMW");
    }

    @Test
    void search_sortsByPriceDesc() {
        when(repository.findAll()).thenReturn(List.of(vw, bmw));

        SearchParams params = new SearchParams();
        params.setSort("priceDesc");

        List<Car> result = service.search(params);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBrand()).isEqualTo("BMW");
        assertThat(result.get(1).getBrand()).isEqualTo("Volkswagen");
    }

    @Test
    void search_excludesUnavailableCars() {
        vw.getRentals().add(new Rental(1, "2026-06-01", "2026-06-10"));
        when(repository.findAll()).thenReturn(List.of(vw, bmw));

        SearchParams params = new SearchParams();
        params.setStartDate("2026-06-05");
        params.setEndDate("2026-06-08");

        List<Car> result = service.search(params);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("BMW");
    }

    @Test
    void create_delegatesToRepository() {
        Car audi = makeCar(null, "Audi", "A4", 85.0, "automatic", 190, "gasoline", 2022, 5);
        Car savedAudi = makeCar(3, "Audi", "A4", 85.0, "automatic", 190, "gasoline", 2022, 5);

        when(repository.save(audi)).thenReturn(savedAudi);

        Car result = service.create(audi);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getBrand()).isEqualTo("Audi");
        verify(repository).save(audi);
    }

    @Test
    void update_throwsWhenCarNotFound() {
        when(repository.update(999, bmw)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, bmw))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void delete_throwsWhenCarNotFound() {
        when(repository.delete(999)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void rent_throwsWhenDatesConflict() {
        vw.getRentals().add(new Rental(1, "2026-06-01", "2026-06-05"));
        when(repository.findById(1)).thenReturn(Optional.of(vw));

        Rental requestedRental = new Rental(null, "2026-06-03", "2026-06-07");

        assertThatThrownBy(() -> service.rent(1, requestedRental))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nicht verfügbar");
    }

    @Test
    void rent_throwsWhenEndBeforeStart() {
        Rental requestedRental = new Rental(null, "2026-06-10", "2026-06-01");

        assertThatThrownBy(() -> service.rent(1, requestedRental))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Enddatum");
    }

    @Test
    void rent_succeedsWhenAvailable() {
        when(repository.findById(1)).thenReturn(Optional.of(vw));

        Rental requestedRental = new Rental(null, "2026-07-01", "2026-07-07");
        Rental savedRental = new Rental(1, "2026-07-01", "2026-07-07");

        when(repository.addRental(1, requestedRental)).thenReturn(Optional.of(savedRental));

        Rental result = service.rent(1, requestedRental);

        assertThat(result.getCarId()).isEqualTo(1);
        assertThat(result.getStartDate()).isEqualTo("2026-07-01");
        assertThat(result.getEndDate()).isEqualTo("2026-07-07");
    }

    private Car makeCar(
            Integer id,
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