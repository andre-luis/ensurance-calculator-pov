package com.insurance.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Schema(description = "Car entity representing a vehicle for insurance coverage")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the car", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Car brand/manufacturer", example = "Toyota", required = true)
    private String brand;
    
    @Schema(description = "Car model", example = "Camry", required = true)
    private String model;
    
    @Schema(description = "Year of manufacture", example = "2022", required = true)
    private int year;
    
    @Schema(description = "Car color", example = "Blue", required = true)
    private String color;
    
    @Schema(description = "Current market price of the car", example = "25000.00", required = true)
    private double marketPrice;

    // Lombok will generate getters, setters, toString, equals, and hashCode methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }
}
