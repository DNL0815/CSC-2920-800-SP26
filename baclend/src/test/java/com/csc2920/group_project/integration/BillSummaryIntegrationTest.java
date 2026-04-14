package com.csc2920.group_project.integration;

import com.csc2920.group_project.dto.BillSummaryDto;
import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.entity.BillSummaryEntity;
import com.csc2920.group_project.repository.LegislationRepository;
import com.csc2920.group_project.repository.BillSummaryRepository;
import com.csc2920.group_project.service.BillSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Bill Summary API Integration Tests")
class BillSummaryIntegrationTest {

    @Autowired
    private LegislationRepository legislationRepository;

    @Autowired
    private BillSummaryRepository billSummaryRepository;

    @Autowired
    private BillSummaryService billSummaryService;

    private LegislationEntity legislation1;
    private LegislationEntity legislation2;
    private LegislationEntity legislation3;

    @BeforeEach
    void setUp() {
        billSummaryRepository.deleteAll();
        legislationRepository.deleteAll();

        // Create legislation entries
        legislation1 = new LegislationEntity();
        legislation1.setCongress(118);
        legislation1.setBillType("hr");
        legislation1.setBillNumber("1234");
        legislation1.setTitle("Test Bill 1");
        legislation1 = legislationRepository.save(legislation1);

        legislation2 = new LegislationEntity();
        legislation2.setCongress(118);
        legislation2.setBillType("s");
        legislation2.setBillNumber("5678");
        legislation2.setTitle("Test Bill 2");
        legislation2 = legislationRepository.save(legislation2);

        legislation3 = new LegislationEntity();
        legislation3.setCongress(119);
        legislation3.setBillType("hr");
        legislation3.setBillNumber("9999");
        legislation3.setTitle("Test Bill 3");
        legislation3 = legislationRepository.save(legislation3);

        // Create bill summaries for legislation1 with multiple versions
        BillSummaryEntity summary1a = new BillSummaryEntity();
        summary1a.setLegislation(legislation1);
        summary1a.setVersionCode("00c");
        summary1a.setActionDate("2023-01-01");
        summary1a.setText("Initial summary");
        summary1a.setUpdateDate("2023-01-01");
        billSummaryRepository.save(summary1a);

        BillSummaryEntity summary1b = new BillSummaryEntity();
        summary1b.setLegislation(legislation1);
        summary1b.setVersionCode("00d");
        summary1b.setActionDate("2023-06-01");
        summary1b.setText("Updated summary with more details");
        summary1b.setUpdateDate("2023-06-01");
        billSummaryRepository.save(summary1b);

        // Create bill summaries for legislation2
        BillSummaryEntity summary2 = new BillSummaryEntity();
        summary2.setLegislation(legislation2);
        summary2.setVersionCode("00c");
        summary2.setActionDate("2023-02-01");
        summary2.setText("Senate bill summary");
        summary2.setUpdateDate("2023-02-01");
        billSummaryRepository.save(summary2);

        // No summaries for legislation3
    }

    @Test
    @DisplayName("Should retrieve summaries for a bill by congress, billType, and billNumber")
    void testGetSummariesForBill() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(118, "hr", "1234");
        
        assertEquals(2, summaries.size());
    }

    @Test
    @DisplayName("Should return empty list when no summaries exist for a bill")
    void testGetSummariesForBillWithNoSummaries() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(119, "hr", "9999");
        
        assertTrue(summaries.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for non-existent bill")
    void testGetSummariesForNonExistentBill() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(999, "hr", "9999");
        
        assertTrue(summaries.isEmpty());
    }

    @Test
    @DisplayName("Should order summaries by update date in descending order")
    void testSummariesOrderedByUpdateDate() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(118, "hr", "1234");
        
        assertEquals(2, summaries.size());
        // Later date should be first (descending order)
        assertEquals("00d", summaries.get(0).versionCode());
        assertEquals("00c", summaries.get(1).versionCode());
    }

    @Test
    @DisplayName("Should preserve summary properties through retrieval")
    void testSummaryPropertiesPreserved() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(118, "hr", "1234");
        
        assertEquals(2, summaries.size());
        BillSummaryDto first = summaries.get(0);
        assertNotNull(first.versionCode());
        assertNotNull(first.actionDate());
        assertNotNull(first.updateDate());
    }

    @Test
    @DisplayName("Should handle multiple bills with different summaries")
    void testMultipleBillsWithDifferentSummaries() {
        List<BillSummaryDto> summaries1 = billSummaryService.getSummaries(118, "hr", "1234");
        List<BillSummaryDto> summaries2 = billSummaryService.getSummaries(118, "s", "5678");
        
        assertEquals(2, summaries1.size());
        assertEquals(1, summaries2.size());
    }

    @Test
    @DisplayName("Should distinguish between different bill types")
    void testDistinguishBillTypes() {
        List<BillSummaryDto> hrBill = billSummaryService.getSummaries(118, "hr", "1234");
        List<BillSummaryDto> sBill = billSummaryService.getSummaries(118, "s", "5678");
        
        assertNotEquals(hrBill.size(), sBill.size());
        assertEquals(2, hrBill.size());
        assertEquals(1, sBill.size());
    }

    @Test
    @DisplayName("Should handle case-insensitive bill type search")
    void testCaseInsensitiveBillTypeSearch() {
        List<BillSummaryDto> summariesLower = billSummaryService.getSummaries(118, "hr", "1234");
        List<BillSummaryDto> summariesUpper = billSummaryService.getSummaries(118, "HR", "1234");
        
        assertEquals(summariesLower.size(), summariesUpper.size());
    }

    @Test
    @DisplayName("Should handle different congress values")
    void testDifferentCongressValues() {
        List<BillSummaryDto> congress118 = billSummaryService.getSummaries(118, "hr", "1234");
        List<BillSummaryDto> congress119 = billSummaryService.getSummaries(119, "hr", "9999");
        
        assertEquals(2, congress118.size());
        assertTrue(congress119.isEmpty());
    }

    @Test
    @DisplayName("Should persist and retrieve summary text")
    void testSummaryTextPersistence() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(118, "hr", "1234");
        
        assertEquals(2, summaries.size());
        assertTrue(summaries.stream().anyMatch(s -> s.text().contains("Initial")));
        assertTrue(summaries.stream().anyMatch(s -> s.text().contains("Updated")));
    }

    @Test
    @DisplayName("Should handle long summary text")
    void testLongSummaryText() {
        String longText = "This is a detailed bill summary. ".repeat(100);
        
        LegislationEntity legislation = new LegislationEntity();
        legislation.setCongress(200);
        legislation.setBillType("hr");
        legislation.setBillNumber("8888");
        legislation = legislationRepository.save(legislation);
        
        BillSummaryEntity summary = new BillSummaryEntity();
        summary.setLegislation(legislation);
        summary.setVersionCode("00c");
        summary.setText(longText);
        summary.setUpdateDate("2024-01-01");
        billSummaryRepository.save(summary);
        
        List<BillSummaryDto> retrieved = billSummaryService.getSummaries(200, "hr", "8888");
        
        assertEquals(1, retrieved.size());
        assertEquals(longText, retrieved.get(0).text());
    }

    @Test
    @DisplayName("Should complete bill summary retrieval end-to-end")
    void testBillSummaryRetrievalEndToEnd() {
        // 1. Verify legislation exists
        Optional<LegislationEntity> leg1 = legislationRepository
                .findByCongressAndBillTypeAndBillNumber(118, "hr", "1234");
        Optional<LegislationEntity> leg2 = legislationRepository
                .findByCongressAndBillTypeAndBillNumber(200, "hr", "8888");
        
        assertTrue(leg1.isPresent());
        
        // 2. Retrieve summaries
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(118, "hr", "1234");
        assertEquals(2, summaries.size());

        // 3. Verify ordering and content
        assertEquals("00d", summaries.get(0).versionCode());
        assertFalse(summaries.get(0).text().isEmpty());
    }

    @Test
    @DisplayName("Should handle no results gracefully")
    void testNoResultsHandling() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(999, "xx", "9999");
        
        assertNotNull(summaries);
        assertTrue(summaries.isEmpty());
    }

    @Test
    @DisplayName("Should support multiple version codes for same bill")
    void testMultipleVersionCodes() {
        List<BillSummaryDto> summaries = billSummaryService.getSummaries(118, "hr", "1234");
        
        assertEquals(2, summaries.size());
        long uniqueVersions = summaries.stream()
                .map(BillSummaryDto::versionCode)
                .distinct()
                .count();
        assertEquals(2, uniqueVersions);
    }
}
