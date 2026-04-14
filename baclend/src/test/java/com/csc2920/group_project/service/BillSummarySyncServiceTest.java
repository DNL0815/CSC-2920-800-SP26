package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.BillSummaryEntity;
import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.repository.BillSummaryRepository;
import com.csc2920.group_project.repository.LegislationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillSummarySyncService Tests")
class BillSummarySyncServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private LegislationRepository legislationRepository;

    @Mock
    private BillSummaryRepository billSummaryRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    private BillSummarySyncService billSummarySyncService;

    @BeforeEach
    void setUp() {
        billSummarySyncService = new BillSummarySyncService(
            restTemplate,
            legislationRepository,
            billSummaryRepository,
            transactionManager,
            "https://api.congress.gov/v3",
            "test-key"
        );
    }

    // ============ syncSummariesForBill() Tests ============

    @Test
    @DisplayName("syncSummariesForBill fetches and saves summaries")
    void testSyncSummariesForBillSuccess() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        
        Map<String, Object> response = Map.of(
            "summaries", List.of(
                createMockSummary("2024-01-15", "Action 1", "Summary text 1"),
                createMockSummary("2024-02-15", "Action 2", "Summary text 2")
            )
        );
        
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(response);

        billSummarySyncService.syncSummariesForBill(bill);

        verify(billSummaryRepository).deleteByLegislation_Id(bill.getId());
        verify(billSummaryRepository).saveAll(any());
    }

    @Test
    @DisplayName("syncSummariesForBill handles null bill data")
    void testSyncSummariesForBillNullBillData() {
        LegislationEntity bill = new LegislationEntity();
        bill.setId(1L);
        bill.setCongress(null);
        bill.setBillType("HR");
        bill.setBillNumber("1234");

        billSummarySyncService.syncSummariesForBill(bill);

        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    @DisplayName("syncSummariesForBill handles null API response")
    void testSyncSummariesForBillNullResponse() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(null);

        billSummarySyncService.syncSummariesForBill(bill);

        verify(restTemplate).getForObject(anyString(), eq(Object.class));
        verify(billSummaryRepository, never()).deleteByLegislation_Id(bill.getId());
    }

    @Test
    @DisplayName("syncSummariesForBill handles empty summaries list")
    void testSyncSummariesForBillEmptySummaries() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        
        Map<String, Object> response = Map.of("summaries", Collections.emptyList());
        
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(response);

        billSummarySyncService.syncSummariesForBill(bill);

        verify(billSummaryRepository).deleteByLegislation_Id(bill.getId());
        verify(billSummaryRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("syncSummariesForBill normalizes bill type to lowercase")
    void testSyncSummariesForBillNormalizeBillType() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        bill.setBillType("HR");
        
        Map<String, Object> response = Map.of("summaries", Collections.emptyList());
        
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(response);

        billSummarySyncService.syncSummariesForBill(bill);

        verify(billSummaryRepository).deleteByLegislation_Id(bill.getId());
    }

    @Test
    @DisplayName("syncSummariesForBill handles different API response formats")
    void testSyncSummariesForBillDifferentResponseFormats() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        
        Map<String, Object> response = Map.of(
            "billSummaries", List.of(
                createMockSummary("2024-01-15", "Action 1", "Summary text 1")
            )
        );
        
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(response);

        billSummarySyncService.syncSummariesForBill(bill);

        verify(billSummaryRepository).deleteByLegislation_Id(bill.getId());
    }

    @Test
    @DisplayName("syncSummariesForBill extracts summary data correctly")
    void testSyncSummariesForBillExtractsSummaryData() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        
        Map<String, Object> summaryData = new java.util.HashMap<>();
        summaryData.put("actionDate", "2024-01-15");
        summaryData.put("actionDesc", "Referred to Committee");
        summaryData.put("text", "This is a test summary");
        summaryData.put("updateDate", "2024-01-16");
        summaryData.put("versionCode", "A");
        
        Map<String, Object> response = Map.of("summaries", List.of(summaryData));
        
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(response);

        billSummarySyncService.syncSummariesForBill(bill);

        verify(billSummaryRepository).saveAll(any());
    }

    @Test
    @DisplayName("syncSummariesForBill handles null version code")
    void testSyncSummariesForBillNullVersionCode() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        
        Map<String, Object> summaryData = new java.util.HashMap<>();
        summaryData.put("actionDate", "2024-01-15");
        summaryData.put("actionDesc", "Referred to Committee");
        summaryData.put("text", "Summary text");
        summaryData.put("updateDate", "2024-01-16");
        summaryData.put("versionCode", null);
        
        Map<String, Object> response = Map.of("summaries", List.of(summaryData));
        
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(response);

        billSummarySyncService.syncSummariesForBill(bill);

        verify(billSummaryRepository).saveAll(any());
    }

    // ============ syncOneAsync() Tests ============

    @Test
    @DisplayName("syncOneAsync finds and syncs bill by congress, type, and number")
    void testSyncOneAsyncSuccess() {
        LegislationEntity bill = createMockBill(118, "HR", "1234");
        
        when(legislationRepository.findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "HR", "1234"))
            .thenReturn(Optional.of(bill));

        billSummarySyncService.syncOneAsync(118, "HR", "1234");

        verify(legislationRepository).findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "HR", "1234");
    }

    @Test
    @DisplayName("syncOneAsync handles bill not found")
    void testSyncOneAsyncBillNotFound() {
        when(legislationRepository.findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "HR", "9999"))
            .thenReturn(Optional.empty());

        billSummarySyncService.syncOneAsync(118, "HR", "9999");

        verify(legislationRepository).findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "HR", "9999");
    }

    // ============ Helper Methods ============

    private LegislationEntity createMockBill(Integer congress, String billType, String billNumber) {
        LegislationEntity bill = new LegislationEntity();
        bill.setId(1L);
        bill.setCongress(congress);
        bill.setBillType(billType);
        bill.setBillNumber(billNumber);
        bill.setTitle("Test Bill");
        return bill;
    }

    private Map<String, Object> createMockSummary(String actionDate, String actionDesc, String text) {
        Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("actionDate", actionDate);
        summary.put("actionDesc", actionDesc);
        summary.put("text", text);
        summary.put("updateDate", actionDate);
        summary.put("versionCode", "A");
        return summary;
    }
}
