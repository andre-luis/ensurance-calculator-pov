package com.insurance.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.demo.model.Car;
import com.insurance.demo.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/register-car")
@Tag(name = "Car Management", description = "APIs for managing car registration and information")
public class CarController {

    @Autowired
    private CarService carService;

    @Operation(summary = "Register a new car", description = "Creates a new car in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> registerCar(@RequestBody Car car) {
        try {
            Car createdCar = carService.registerCar(car);
            return ResponseEntity.status(201).body(createdCar);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid input");
        }
    }

    @Operation(summary = "Get all covered cars", description = "Retrieves a list of all cars that can be covered by insurance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of covered cars",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/covered-cars")
    public ResponseEntity<List<Car>> getCoveredCars() {
        try {
            List<Car> coveredCars = carService.getCoveredCars();
            return ResponseEntity.ok(coveredCars);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
