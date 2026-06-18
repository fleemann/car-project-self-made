package com.example.carrental.model;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Datenmodell fuer eine Mietbuchung.
 * Kennt ihr Auto ueber die {@code carId} und haelt Start- und Enddatum als Text.
 */
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

    /**
     * Prueft, ob diese Buchung sich mit einem anderen Zeitraum ueberschneidet.
     * Umgekehrt gedacht: keine Ueberschneidung, wenn ein Zeitraum ganz vor dem
     * anderen liegt.
     *
     * @param start Beginn des zu pruefenden Zeitraums
     * @param end   Ende des zu pruefenden Zeitraums
     * @return {@code true}, wenn sich die Zeitraeume ueberschneiden
     */
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