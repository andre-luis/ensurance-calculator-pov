package com.insurance.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for insurance premium calculation")
public class InsuranceRequest {
    
    @Schema(description = "ID of the customer requesting insurance", example = "1", required = true)
    private Long userId;
    
    @Schema(description = "ID of the car to be insured", example = "1", required = true)
    private Long carId;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}