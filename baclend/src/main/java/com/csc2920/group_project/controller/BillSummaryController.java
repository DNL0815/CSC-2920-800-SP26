package com.csc2920.group_project.controller;

import com.csc2920.group_project.dto.BillSummaryDto;
import com.csc2920.group_project.service.BillSummaryService;
import com.csc2920.group_project.service.BillSummarySyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillSummaryController {

    private final BillSummaryService billSummaryService;
    private final BillSummarySyncService billSummarySyncService;

    public BillSummaryController(BillSummaryService billSummaryService,
                                 BillSummarySyncService billSummarySyncService) {
        this.billSummaryService = billSummaryService;
        this.billSummarySyncService = billSummarySyncService;
    }

    // DB-backed retrieval (no Congress API call)
    @GetMapping("/{congress}/{billType}/{billNumber}/summaries")
    public ResponseEntity<?> getSummaries(@PathVariable Integer congress,
                                          @PathVariable String billType,
                                          @PathVariable String billNumber) {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(congress, billType, billNumber);
        if (summaries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No bill summaries are available for this bill.");
        }
        return ResponseEntity.ok(summaries);
    }

    // Sync summaries for all bills already in legislation table
    @PostMapping("/summaries/sync/all")
    public String syncAll() {
        billSummarySyncService.syncAllSummariesAsync();
        return "Bill summary sync dispatched.";
    }

    // Sync summaries for one bill (must already exist in legislation table)
    @PostMapping("/{congress}/{billType}/{billNumber}/summaries/sync")
    public String syncOne(@PathVariable Integer congress,
                          @PathVariable String billType,
                          @PathVariable String billNumber) {
        billSummarySyncService.syncOneAsync(congress, billType, billNumber);
        return "Bill summary sync dispatched for " + congress + " " + billType + " " + billNumber;
    }
}
