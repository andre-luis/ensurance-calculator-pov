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

import com.insurance.demo.model.Customer;
import com.insurance.demo.service.CustomerService;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterCustomerSuccess() {
        Customer customer = new Customer();
        // Set customer properties
        when(customerService.registerCustomer(any(Customer.class))).thenReturn(customer);

        ResponseEntity<?> response = customerController.registerCustomer(customer);

        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody() == customer;
    }

    @Test
    void testRegisterCustomerError() {
        Customer customer = new Customer();
        // Set customer properties
        when(customerService.registerCustomer(any(Customer.class))).thenThrow(new RuntimeException("Invalid input"));

        ResponseEntity<?> response = customerController.registerCustomer(customer);

        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        Object responseBody = response.getBody();
        assert responseBody != null && responseBody.equals("Invalid input");
    }
}
