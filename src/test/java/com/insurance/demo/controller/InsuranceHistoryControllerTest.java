package com.insurance.demo.controller;

import com.insurance.demo.model.PremiumHistoryEntry;
import com.insurance.demo.service.PremiumHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class InsuranceHistoryControllerTest {

    @Mock
    private PremiumHistoryService premiumHistoryService;

    @InjectMocks
    private InsuranceHistoryController insuranceHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetHistoryWithNoCalculationsReturnsEmptyList() {
        when(premiumHistoryService.getHistory()).thenReturn(Collections.emptyList());

        ResponseEntity<List<PremiumHistoryEntry>> response = insuranceHistoryController.getHistory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetHistoryReturnsEntriesWithCorrectFields() {
        PremiumHistoryEntry entry = new PremiumHistoryEntry();
        entry.setTimestamp(LocalDateTime.of(2026, 5, 26, 10, 0));
        entry.setCustomerId(1L);
        entry.setCarId(2L);
        entry.setMonthlyPremium(150.0);

        when(premiumHistoryService.getHistory()).thenReturn(List.of(entry));

        ResponseEntity<List<PremiumHistoryEntry>> response = insuranceHistoryController.getHistory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        PremiumHistoryEntry returned = response.getBody().get(0);
        assertEquals(1L, returned.getCustomerId());
        assertEquals(2L, returned.getCarId());
        assertEquals(150.0, returned.getMonthlyPremium());
    }
}
