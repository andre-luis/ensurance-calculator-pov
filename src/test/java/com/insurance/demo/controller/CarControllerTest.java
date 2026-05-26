package com.insurance.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.insurance.demo.model.Car;
import com.insurance.demo.service.CarService;

class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterCarSuccess() {
        Car car = new Car();
        // Set car properties
        when(carService.registerCar(any(Car.class))).thenReturn(car);

        ResponseEntity<?> response = carController.registerCar(car);

        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody() == car;
    }

    @Test
    void testRegisterCarError() {
        Car car = new Car();
        // Set car properties
        when(carService.registerCar(any(Car.class))).thenThrow(new RuntimeException("Invalid input"));

        ResponseEntity<?> response = carController.registerCar(car);

        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        Object responseBody = response.getBody();
        assert responseBody != null && responseBody.equals("Invalid input");
    }
}
