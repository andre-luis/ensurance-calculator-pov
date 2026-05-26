package com.insurance.demo.service;

import java.time.LocalDateTime;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insurance.demo.model.Car;
import com.insurance.demo.model.Customer;
import com.insurance.demo.model.InsuranceCalculation;
import com.insurance.demo.model.PremiumHistoryEntry;
import com.insurance.demo.repository.CarRepository;
import com.insurance.demo.repository.CustomerRepository;
import com.insurance.demo.repository.InsuranceCalculationRepository;

@Service
public class InsuranceCalculationService {

    @Autowired
    private InsuranceCalculationRepository insuranceCalculationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PremiumHistoryService premiumHistoryService;

    public InsuranceCalculation calculateInsurance(Long userId, Long carId) {
        // Find the car by its ID
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Car not found"));

        // Find the customer by its ID
        Customer customer = customerRepository.findById(userId).orElseThrow(() -> new RuntimeException("Customer not found"));

        // Business logic to calculate the insurance premium
        InsuranceCalculation insuranceCalculation = new InsuranceCalculation();
        insuranceCalculation.setCar(car);
        insuranceCalculation.setCustomer(customer);
        
        double monthlyPremium = calculatePremium(insuranceCalculation);
        insuranceCalculation.setMonthlyPremium(monthlyPremium);
        InsuranceCalculation saved = insuranceCalculationRepository.save(insuranceCalculation);

        PremiumHistoryEntry entry = new PremiumHistoryEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setCustomerId(customer.getId());
        entry.setCarId(car.getId());
        entry.setMonthlyPremium(saved.getMonthlyPremium());
        premiumHistoryService.record(entry);

        return saved;
    }
    
    private double calculatePremium(InsuranceCalculation insuranceCalculation) {
        double premium;
        double carPrice = insuranceCalculation.getCar().getMarketPrice();
        int carYear = insuranceCalculation.getCar().getYear();
        String address = insuranceCalculation.getCustomer().getAddress();
        // get the difference between the current year and the car year
        //use system date to get the current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        
        int age = currentYear - carYear;
              
        if (age > 5) {
            premium = carPrice * 0.004;
        } else {
            if ("Sao Paulo".equalsIgnoreCase(address)) {
                premium = carPrice * 0.01;
            } else {
                premium = carPrice * 0.005;
            }
        }

        return premium;
    }
       
}
