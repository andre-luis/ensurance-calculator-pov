package com.insurance.demo.service;

import com.insurance.demo.model.PremiumHistoryEntry;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PremiumHistoryService {

    private final CopyOnWriteArrayList<PremiumHistoryEntry> history = new CopyOnWriteArrayList<>();

    public void record(PremiumHistoryEntry entry) {
        history.add(0, entry);
    }

    public List<PremiumHistoryEntry> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
