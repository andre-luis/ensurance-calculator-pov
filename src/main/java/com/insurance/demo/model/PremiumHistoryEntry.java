package com.insurance.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "A single premium calculation history entry")
public class PremiumHistoryEntry {

    @Schema(description = "Timestamp when the premium was calculated", example = "2026-05-26T10:15:30", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime timestamp;

    @Schema(description = "ID of the customer for whom the premium was calculated", example = "1")
    private Long customerId;

    @Schema(description = "ID of the car for which the premium was calculated", example = "1")
    private Long carId;

    @Schema(description = "Calculated monthly premium amount", example = "125.50")
    private double monthlyPremium;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public double getMonthlyPremium() {
        return monthlyPremium;
    }

    public void setMonthlyPremium(double monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
    }
}
