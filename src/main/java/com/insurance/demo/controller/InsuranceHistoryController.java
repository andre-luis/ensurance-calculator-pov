package com.insurance.demo.controller;

import com.insurance.demo.model.PremiumHistoryEntry;
import com.insurance.demo.service.PremiumHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/insurance-history")
@Tag(name = "Insurance History", description = "APIs for retrieving insurance calculation history")
public class InsuranceHistoryController {

    @Autowired
    private PremiumHistoryService premiumHistoryService;

    @Operation(summary = "Get premium calculation history", description = "Returns all premiums calculated in the current server session, newest first")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PremiumHistoryEntry.class)))
    @GetMapping
    public ResponseEntity<List<PremiumHistoryEntry>> getHistory() {
        return ResponseEntity.ok(premiumHistoryService.getHistory());
    }
}
