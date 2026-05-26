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

import com.insurance.demo.dto.InsuranceRequest;
import com.insurance.demo.exception.CarNotFoundException;
import com.insurance.demo.exception.CustomerNotFoundException;
import com.insurance.demo.model.InsuranceCalculation;
import com.insurance.demo.service.InsuranceCalculationService;

class InsuranceCalculationControllerTest {

    @Mock
    private InsuranceCalculationService insuranceCalculationService;

    @InjectMocks
    private InsuranceCalculationController insuranceCalculationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateInsuranceSuccess() {
        InsuranceRequest request = new InsuranceRequest();
        request.setUserId(1L);
        request.setCarId(1L);

        InsuranceCalculation calculation = new InsuranceCalculation();
        // Set calculation properties

        when(insuranceCalculationService.calculateInsurance(any(Long.class), any(Long.class))).thenReturn(calculation);

        ResponseEntity<?> response = insuranceCalculationController.calculateInsurance(request);

        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() == calculation;
    }

    @Test
    void testCalculateInsuranceCarNotFound() {
        InsuranceRequest request = new InsuranceRequest();
        request.setUserId(1L);
        request.setCarId(1L);

        when(insuranceCalculationService.calculateInsurance(any(Long.class), any(Long.class))).thenThrow(new CarNotFoundException("Car not found"));

        ResponseEntity<?> response = insuranceCalculationController.calculateInsurance(request);

        assert response.getStatusCode() == HttpStatus.NOT_FOUND;
        Object responseBody = response.getBody();
        assert responseBody != null && responseBody.equals("Car not found");
    }

    @Test
    void testCalculateInsuranceCustomerNotFound() {
        InsuranceRequest request = new InsuranceRequest();
        request.setUserId(1L);
        request.setCarId(1L);

        when(insuranceCalculationService.calculateInsurance(any(Long.class), any(Long.class))).thenThrow(new CustomerNotFoundException("Customer not found"));

        ResponseEntity<?> response = insuranceCalculationController.calculateInsurance(request);

        assert response.getStatusCode() == HttpStatus.NOT_FOUND;
        Object responseBody = response.getBody();
        assert responseBody != null && responseBody.equals("Customer not found");
    }

    @Test
    void testCalculateInsuranceInvalidInput() {
        InsuranceRequest request = new InsuranceRequest();
        request.setUserId(1L);
        request.setCarId(1L);

        when(insuranceCalculationService.calculateInsurance(any(Long.class), any(Long.class))).thenThrow(new RuntimeException("Invalid input"));

        ResponseEntity<?> response = insuranceCalculationController.calculateInsurance(request);

        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        Object responseBody = response.getBody();
        assert responseBody != null && responseBody.equals("Invalid input");
    }
}
