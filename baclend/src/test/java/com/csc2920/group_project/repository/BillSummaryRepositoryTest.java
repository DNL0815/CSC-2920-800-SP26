package com.csc2920.group_project.repository;

import com.csc2920.group_project.entity.BillSummaryEntity;
import com.csc2920.group_project.entity.LegislationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("BillSummaryRepository Tests")
class BillSummaryRepositoryTest {

    @Autowired
    private BillSummaryRepository billSummaryRepository;

    @Autowired
    private LegislationRepository legislationRepository;

    private LegislationEntity bill1;
    private LegislationEntity bill2;
    private BillSummaryEntity summary1;
    private BillSummaryEntity summary2;
    private BillSummaryEntity summary3;

    @BeforeEach
    void setUp() {
        bill1 = new LegislationEntity();
        bill1.setCongress(118);
        bill1.setBillType("HR");
        bill1.setBillNumber("1234");
        bill1.setTitle("Test Bill 1");
        legislationRepository.save(bill1);

        bill2 = new LegislationEntity();
        bill2.setCongress(118);
        bill2.setBillType("S");
        bill2.setBillNumber("5678");
        bill2.setTitle("Test Senate Bill");
        legislationRepository.save(bill2);

        summary1 = new BillSummaryEntity();
        summary1.setLegislation(bill1);
        summary1.setActionDate("2024-01-15");
        summary1.setActionDesc("Introduced");
        summary1.setText("Summary text 1");
        summary1.setUpdateDate("2024-01-16");
        billSummaryRepository.save(summary1);

        summary2 = new BillSummaryEntity();
        summary2.setLegislation(bill1);
        summary2.setActionDate("2024-02-20");
        summary2.setActionDesc("Referred to Committee");
        summary2.setText("Summary text 2");
        summary2.setUpdateDate("2024-02-21");
        billSummaryRepository.save(summary2);

        summary3 = new BillSummaryEntity();
        summary3.setLegislation(bill1);
        summary3.setActionDate("2024-03-10");
        summary3.setActionDesc("Passed");
        summary3.setText("Summary text 3");
        summary3.setUpdateDate("2024-03-11");
        billSummaryRepository.save(summary3);
    }

    // ============ findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumber... Tests ============

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumber returns summaries")
    void testFindByLegislationFieldsSuccess() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "1234");

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumber orders by update date descending")
    void testFindByLegislationFieldsOrdering() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "1234");

        assertEquals(3, result.size());
        assertEquals("2024-03-11", result.get(0).getUpdateDate());
        assertEquals("2024-02-21", result.get(1).getUpdateDate());
        assertEquals("2024-01-16", result.get(2).getUpdateDate());
    }

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumber returns empty when not found")
    void testFindByLegislationFieldsNotFound() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "9999");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumber is case-sensitive")
    void testFindByLegislationFieldsCaseSensitive() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "hr", "1234");

        assertTrue(result.isEmpty());
    }

    // ============ findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumber... Tests ============

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumber returns summaries")
    void testFindByLegislationFieldsIgnoreCaseSuccess() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "1234");

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumber ignores case")
    void testFindByLegislationFieldsIgnoreCaseLowercase() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "hr", "1234");

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumber handles mixed case")
    void testFindByLegislationFieldsIgnoreCaseMixedCase() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "Hr", "1234");

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumber orders by update date descending")
    void testFindByLegislationFieldsIgnoreCaseOrdering() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "hr", "1234");

        assertEquals("2024-03-11", result.get(0).getUpdateDate());
        assertEquals("2024-02-21", result.get(1).getUpdateDate());
        assertEquals("2024-01-16", result.get(2).getUpdateDate());
    }

    @Test
    @DisplayName("findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumber returns empty when not found")
    void testFindByLegislationFieldsIgnoreCaseNotFound() {
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "9999");

        assertTrue(result.isEmpty());
    }

    // ============ deleteByLegislation_Id() Tests ============

    @Test
    @DisplayName("deleteByLegislation_Id removes all summaries for bill")
    void testDeleteByLegislationIdSuccess() {
        billSummaryRepository.deleteByLegislation_Id(bill1.getId());

        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "1234");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deleteByLegislation_Id does not affect other bills")
    void testDeleteByLegislationIdDoesNotAffectOtherBills() {
        billSummaryRepository.deleteByLegislation_Id(bill1.getId());

        List<BillSummaryEntity> result = billSummaryRepository.findAll();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("deleteByLegislation_Id handles non-existent bill")
    void testDeleteByLegislationIdNonExistent() {
        billSummaryRepository.deleteByLegislation_Id(999L);

        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "1234");

        assertEquals(3, result.size());
    }

    // ============ CRUD Operations ============

    @Test
    @DisplayName("save persists summary to database")
    void testSaveSummary() {
        BillSummaryEntity newSummary = new BillSummaryEntity();
        newSummary.setLegislation(bill2);
        newSummary.setActionDate("2024-04-01");
        newSummary.setActionDesc("Test Action");
        newSummary.setText("Test summary text");
        newSummary.setUpdateDate("2024-04-02");

        billSummaryRepository.save(newSummary);
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "S", "5678");

        assertEquals(1, result.size());
        assertEquals("Test Action", result.get(0).getActionDesc());
    }

    @Test
    @DisplayName("save updates existing summary")
    void testUpdateSummary() {
        summary1.setText("Updated text");
        billSummaryRepository.save(summary1);

        BillSummaryEntity retrieved = billSummaryRepository.findById(summary1.getId()).get();

        assertEquals("Updated text", retrieved.getText());
    }

    @Test
    @DisplayName("delete removes summary from database")
    void testDeleteSummary() {
        billSummaryRepository.delete(summary1);
        List<BillSummaryEntity> result = billSummaryRepository
            .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                118, "HR", "1234");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findById returns summary when found")
    void testFindById() {
        var result = billSummaryRepository.findById(summary1.getId());

        assertTrue(result.isPresent());
        assertEquals("Introduced", result.get().getActionDesc());
    }

    @Test
    @DisplayName("findAll returns all summaries")
    void testFindAll() {
        var result = billSummaryRepository.findAll();

        assertEquals(3, result.size());
    }
}
