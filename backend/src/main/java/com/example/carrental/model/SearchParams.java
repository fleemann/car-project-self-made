package com.example.carrental.model;

/**
 * Buendelt alle Query-Parameter der Auto-Suche in einem Objekt.
 * Die Zahl-Felder sind absichtlich Wrapper-Typen ({@code Double}/{@code Integer}):
 * {@code null} bedeutet "Filter nicht gesetzt" und wirkt dann nicht.
 */
public class SearchParams {

    private String brand;
    private String model;
    private String gear;
    private String engine;
    private Double minPrice;
    private Double maxPrice;
    private Integer psMin;
    private Integer psMax;
    private Integer yearMin;
    private Integer seats;
    private String startDate;
    private String endDate;
    private String sort = "priceAsc";

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

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getPsMin() {
        return psMin;
    }

    public void setPsMin(Integer psMin) {
        this.psMin = psMin;
    }

    public Integer getPsMax() {
        return psMax;
    }

    public void setPsMax(Integer psMax) {
        this.psMax = psMax;
    }

    public Integer getYearMin() {
        return yearMin;
    }

    public void setYearMin(Integer yearMin) {
        this.yearMin = yearMin;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}