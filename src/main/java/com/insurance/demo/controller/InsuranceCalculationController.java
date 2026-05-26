package com.insurance.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.demo.dto.InsuranceRequest;
import com.insurance.demo.exception.CarNotFoundException;
import com.insurance.demo.exception.CustomerNotFoundException;
import com.insurance.demo.model.InsuranceCalculation;
import com.insurance.demo.service.InsuranceCalculationService;

@RestController
@RequestMapping("/calculate-insurance")
@Tag(name = "Insurance Calculation", description = "APIs for calculating insurance premiums")
public class InsuranceCalculationController {

    @Autowired
    private InsuranceCalculationService insuranceCalculationService;

    @Operation(summary = "Calculate insurance premium", description = "Calculates insurance premium for a specific customer and car combination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insurance premium calculated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsuranceCalculation.class))),
            @ApiResponse(responseCode = "404", description = "Customer or car not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> calculateInsurance(@RequestBody InsuranceRequest insuranceRequest) {
        try {
            InsuranceCalculation calculatedInsurance = insuranceCalculationService.calculateInsurance(insuranceRequest.getUserId(), insuranceRequest.getCarId());
            return ResponseEntity.status(200).body(calculatedInsurance);
        } catch (CarNotFoundException | CustomerNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid input");
        }
    }
}
