package com.example.carrental.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

public class Car {

    private Integer id;

    @NotBlank(message = "Marke ist erforderlich")
    private String brand;

    @NotBlank(message = "Modell ist erforderlich")
    private String model;

    @Positive(message = "Preis muss positiv sein")
    private double price;

    @NotBlank(message = "Getriebe ist erforderlich")
    @Pattern(regexp = "automatic|manual", message = "Getriebe muss 'automatic' oder 'manual' sein")
    private String gear;

    @Positive(message = "PS muss positiv sein")
    private int ps;

    @NotBlank(message = "Motor ist erforderlich")
    @Pattern(regexp = "electro|gasoline|hybrid", message = "Motor muss 'electro', 'gasoline' oder 'hybrid' sein")
    private String engine;

    @Min(value = 1901, message = "Baujahr muss nach 1900 sein")
    private int year;

    @Positive(message = "Sitzanzahl muss positiv sein")
    private int seats;

    private List<Rental> rentals = new ArrayList<>();

    public Car() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }
}