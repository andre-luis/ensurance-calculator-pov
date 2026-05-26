package com.insurance.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Schema(description = "Insurance calculation result containing premium details")
public class InsuranceCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the insurance calculation", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @Schema(description = "Customer for whom insurance is calculated")
    private Customer customer;

    @ManyToOne
    @Schema(description = "Car for which insurance is calculated")
    private Car car;

    @Schema(description = "Calculated monthly premium amount", example = "125.50", accessMode = Schema.AccessMode.READ_ONLY)
    private double monthlyPremium;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public double getMonthlyPremium() {
        return monthlyPremium;
    }

    public void setMonthlyPremium(double monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
    }
    // Lombok will generate getters, setters, toString, equals, and hashCode methods
}
