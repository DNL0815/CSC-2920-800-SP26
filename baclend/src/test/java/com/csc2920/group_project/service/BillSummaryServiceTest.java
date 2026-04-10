package com.csc2920.group_project.service;

import com.csc2920.group_project.dto.BillSummaryDto;
import com.csc2920.group_project.entity.BillSummaryEntity;
import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.mapper.BillSummaryMapper;
import com.csc2920.group_project.repository.BillSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillSummaryService Tests")
class BillSummaryServiceTest {

    @Mock
    private BillSummaryRepository billSummaryRepository;

    private BillSummaryService billSummaryService;

    @BeforeEach
    void setUp() {
        billSummaryService = new BillSummaryService(billSummaryRepository);
    }

    // ============ getSummaries() Tests ============

    @Test
    @DisplayName("getSummaries returns bill summaries for bill")
    void testGetSummariesSuccess() {
        Integer congress = 118;
        String billType = "HR";
        String billNumber = "1234";
        
        List<BillSummaryEntity> mockEntities = createMockBillSummaryEntities(3, congress, billType, billNumber);
        
        when(billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                congress, billType, billNumber))
            .thenReturn(mockEntities);

        List<BillSummaryDto> result = billSummaryService.getSummaries(congress, billType, billNumber);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(billSummaryRepository)
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                congress, billType, billNumber);
    }

    @Test
    @DisplayName("getSummaries returns empty list when no summaries found")
    void testGetSummariesEmpty() {
        Integer congress = 118;
        String billType = "HR";
        String billNumber = "9999";
        
        when(billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                congress, billType, billNumber))
            .thenReturn(Collections.emptyList());

        List<BillSummaryDto> result = billSummaryService.getSummaries(congress, billType, billNumber);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getSummaries handles case-insensitive bill type")
    void testGetSummariesCaseInsensitive() {
        Integer congress = 118;
        String billType = "HR";
        String billNumber = "1234";
        
        List<BillSummaryEntity> mockEntities = createMockBillSummaryEntities(2, congress, billType, billNumber);
        
        when(billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                anyInt(), anyString(), anyString()))
            .thenReturn(mockEntities);

        List<BillSummaryDto> result1 = billSummaryService.getSummaries(congress, "hr", billNumber);
        List<BillSummaryDto> result2 = billSummaryService.getSummaries(congress, "HR", billNumber);
        List<BillSummaryDto> result3 = billSummaryService.getSummaries(congress, "Hr", billNumber);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
    }

    @Test
    @DisplayName("getSummaries returns summaries ordered by update date descending")
    void testGetSummariesOrdering() {
        Integer congress = 118;
        String billType = "HR";
        String billNumber = "1234";
        
        BillSummaryEntity entity1 = new BillSummaryEntity();
        entity1.setUpdateDate("2024-03-01");
        entity1.setActionDesc("Action 1");
        
        BillSummaryEntity entity2 = new BillSummaryEntity();
        entity2.setUpdateDate("2024-02-01");
        entity2.setActionDesc("Action 2");
        
        BillSummaryEntity entity3 = new BillSummaryEntity();
        entity3.setUpdateDate("2024-01-01");
        entity3.setActionDesc("Action 3");
        
        List<BillSummaryEntity> mockEntities = List.of(entity1, entity2, entity3);
        
        when(billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                congress, billType, billNumber))
            .thenReturn(mockEntities);

        List<BillSummaryDto> result = billSummaryService.getSummaries(congress, billType, billNumber);

        assertEquals(3, result.size());
        assertEquals("2024-03-01", result.get(0).updateDate());
        assertEquals("2024-02-01", result.get(1).updateDate());
        assertEquals("2024-01-01", result.get(2).updateDate());
    }

    @Test
    @DisplayName("getSummaries handles null congress parameter")
    void testGetSummariesNullCongress() {
        when(billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                null, "HR", "1234"))
            .thenReturn(Collections.emptyList());

        List<BillSummaryDto> result = billSummaryService.getSummaries(null, "HR", "1234");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ============ Helper Methods ============

    private List<BillSummaryEntity> createMockBillSummaryEntities(int count, Integer congress, String billType, String billNumber) {
        List<BillSummaryEntity> entities = new java.util.ArrayList<>();
        
        LegislationEntity legislation = new LegislationEntity();
        legislation.setId(1L);
        legislation.setCongress(congress);
        legislation.setBillType(billType);
        legislation.setBillNumber(billNumber);
        
        for (int i = 0; i < count; i++) {
            BillSummaryEntity entity = new BillSummaryEntity();
            entity.setId((long) i);
            entity.setLegislation(legislation);
            entity.setActionDate("2024-0" + (1 + i) + "-01");
            entity.setActionDesc("Summary Action " + i);
            entity.setText("This is summary text " + i);
            entity.setUpdateDate("2024-0" + (1 + i) + "-01");
            entity.setVersionCode("A");
            
            entities.add(entity);
        }
        
        return entities;
    }
}
