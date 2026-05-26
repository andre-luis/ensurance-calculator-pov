package com.insurance.demo.service;

import com.insurance.demo.model.PremiumHistoryEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PremiumHistoryServiceTest {

    private PremiumHistoryService premiumHistoryService;

    @BeforeEach
    void setUp() {
        premiumHistoryService = new PremiumHistoryService();
    }

    @Test
    void testEmptyHistoryReturnsEmptyList() {
        List<PremiumHistoryEntry> history = premiumHistoryService.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void testOneRecordCallReturnsOneEntry() {
        PremiumHistoryEntry entry = buildEntry(1L, 1L, 100.0);
        premiumHistoryService.record(entry);

        List<PremiumHistoryEntry> history = premiumHistoryService.getHistory();
        assertEquals(1, history.size());
        assertEquals(entry, history.get(0));
    }

    @Test
    void testTwoRecordCallsReturnNewestFirst() {
        PremiumHistoryEntry first = buildEntry(1L, 1L, 100.0);
        first.setTimestamp(LocalDateTime.now().minusSeconds(5));

        PremiumHistoryEntry second = buildEntry(2L, 2L, 200.0);
        second.setTimestamp(LocalDateTime.now());

        premiumHistoryService.record(first);
        premiumHistoryService.record(second);

        List<PremiumHistoryEntry> history = premiumHistoryService.getHistory();
        assertEquals(2, history.size());
        assertEquals(second, history.get(0));
        assertEquals(first, history.get(1));
    }

    private PremiumHistoryEntry buildEntry(Long customerId, Long carId, double premium) {
        PremiumHistoryEntry entry = new PremiumHistoryEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setCustomerId(customerId);
        entry.setCarId(carId);
        entry.setMonthlyPremium(premium);
        return entry;
    }
}
