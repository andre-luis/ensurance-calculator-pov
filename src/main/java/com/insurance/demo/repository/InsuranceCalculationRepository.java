package com.insurance.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.insurance.demo.model.InsuranceCalculation;

@Repository
public interface InsuranceCalculationRepository extends JpaRepository<InsuranceCalculation, Long> {
}
