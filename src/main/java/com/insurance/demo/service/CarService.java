package com.insurance.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insurance.demo.model.Car;
import com.insurance.demo.repository.CarRepository;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public Car registerCar(Car car) {
        return carRepository.save(car);
    }

    public List<Car> getCoveredCars() {
        return carRepository.findAll();
    }
}
