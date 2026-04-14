package com.csc2920.group_project.controller;

import com.csc2920.group_project.dto.BillSummaryDto;
import com.csc2920.group_project.service.BillSummaryService;
import com.csc2920.group_project.service.BillSummarySyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Bill Summary Controller Tests")
class BillSummaryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BillSummaryService billSummaryService;

    @Mock
    private BillSummarySyncService billSummarySyncService;

    private BillSummaryDto testSummary;

    @BeforeEach
    void setUp() {
        testSummary = new BillSummaryDto("2023-02-20", "Passed House", 
                "This is a test bill summary", "2023-02-20", "A");

        mockMvc = MockMvcBuilders.standaloneSetup(
                new BillSummaryController(billSummaryService, billSummarySyncService)
        ).build();
    }

    @Test
    @DisplayName("GET /api/bills/{congress}/{billType}/{billNumber}/summaries - Should return bill summaries")
    void testGetSummaries() throws Exception {
        Integer congress = 118;
        String billType = "hr";
        String billNumber = "1234";

        BillSummaryDto summary2 = new BillSummaryDto("2023-03-15", "Passed Senate", 
                "Another summary", "2023-03-15", "B");

        List<BillSummaryDto> summaries = Arrays.asList(testSummary, summary2);
        when(billSummaryService.getSummaries(congress, billType, billNumber))
                .thenReturn(summaries);

        mockMvc.perform(get("/api/bills/{congress}/{billType}/{billNumber}/summaries",
                congress, billType, billNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text").value("This is a test bill summary"))
                .andExpect(jsonPath("$[1].text").value("Another summary"));

        verify(billSummaryService, times(1)).getSummaries(congress, billType, billNumber);
    }

    @Test
    @DisplayName("GET /api/bills/{congress}/{billType}/{billNumber}/summaries - Should return 404 when no summaries found")
    void testGetSummariesNotFound() throws Exception {
        Integer congress = 118;
        String billType = "hr";
        String billNumber = "9999";

        when(billSummaryService.getSummaries(congress, billType, billNumber))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bills/{congress}/{billType}/{billNumber}/summaries",
                congress, billType, billNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No bill summaries are available for this bill."));

        verify(billSummaryService, times(1)).getSummaries(congress, billType, billNumber);
    }

    @Test
    @DisplayName("POST /api/bills/summaries/sync/all - Should dispatch sync for all bill summaries")
    void testSyncAllSummaries() throws Exception {
        doNothing().when(billSummarySyncService).syncAllSummariesAsync();

        mockMvc.perform(post("/api/bills/summaries/sync/all"))
                .andExpect(status().isOk())
                .andExpect(content().string("Bill summary sync dispatched."));

        verify(billSummarySyncService, times(1)).syncAllSummariesAsync();
    }

    @Test
    @DisplayName("POST /api/bills/{congress}/{billType}/{billNumber}/summaries/sync - Should dispatch sync for specific bill")
    void testSyncOneSummary() throws Exception {
        Integer congress = 118;
        String billType = "hr";
        String billNumber = "1234";

        doNothing().when(billSummarySyncService).syncOneAsync(congress, billType, billNumber);

        mockMvc.perform(post("/api/bills/{congress}/{billType}/{billNumber}/summaries/sync",
                congress, billType, billNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("Bill summary sync dispatched for " + congress + " " + billType + " " + billNumber));

        verify(billSummarySyncService, times(1)).syncOneAsync(congress, billType, billNumber);
    }

    @Test
    @DisplayName("GET /api/bills/{congress}/{billType}/{billNumber}/summaries - Should handle different bill types")
    void testGetSummariesWithDifferentBillTypes() throws Exception {
        Integer congress = 118;
        String billType = "s";
        String billNumber = "5678";

        BillSummaryDto senateSummary = new BillSummaryDto("2023-02-20", "Passed Senate", 
                "Senate bill text", "2023-02-20", "A");

        when(billSummaryService.getSummaries(congress, billType, billNumber))
                .thenReturn(Arrays.asList(senateSummary));

        mockMvc.perform(get("/api/bills/{congress}/{billType}/{billNumber}/summaries",
                congress, billType, billNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("Senate bill text"));

        verify(billSummaryService, times(1)).getSummaries(congress, billType, billNumber);
    }

    @Test
    @DisplayName("GET /api/bills/{congress}/{billType}/{billNumber}/summaries - Should handle different congresses")
    void testGetSummariesWithDifferentCongress() throws Exception {
        Integer congress = 117;
        String billType = "hr";
        String billNumber = "1234";

        List<BillSummaryDto> summaries = Arrays.asList(testSummary);
        when(billSummaryService.getSummaries(congress, billType, billNumber))
                .thenReturn(summaries);

        mockMvc.perform(get("/api/bills/{congress}/{billType}/{billNumber}/summaries",
                congress, billType, billNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(billSummaryService, times(1)).getSummaries(congress, billType, billNumber);
    }
}
