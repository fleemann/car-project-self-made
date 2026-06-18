package com.example.carrental.model;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class Rental {

    private Integer carId;

    @NotBlank(message = "Startdatum ist erforderlich")
    private String startDate;

    @NotBlank(message = "Enddatum ist erforderlich")
    private String endDate;

    public Rental() {
    }

    public Rental(Integer carId, String startDate, String endDate) {
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean overlaps(LocalDate start, LocalDate end) {
        LocalDate existingStart = LocalDate.parse(this.startDate);
        LocalDate existingEnd = LocalDate.parse(this.endDate);

        return !existingEnd.isBefore(start) && !existingStart.isAfter(end);
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}