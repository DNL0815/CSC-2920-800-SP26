package com.csc2920.group_project.repository;

import com.csc2920.group_project.entity.LegislationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("LegislationRepository Tests")
class LegislationRepositoryTest {

    @Autowired
    private LegislationRepository legislationRepository;

    private LegislationEntity bill1;
    private LegislationEntity bill2;
    private LegislationEntity bill3;

    @BeforeEach
    void setUp() {
        bill1 = new LegislationEntity();
        bill1.setCongress(118);
        bill1.setBillType("HR");
        bill1.setBillNumber("1234");
        bill1.setTitle("Test Bill 1");
        bill1.setIntroducedDate("2023-01-15");

        bill2 = new LegislationEntity();
        bill2.setCongress(118);
        bill2.setBillType("S");
        bill2.setBillNumber("5678");
        bill2.setTitle("Test Senate Bill");
        bill2.setIntroducedDate("2023-02-20");

        bill3 = new LegislationEntity();
        bill3.setCongress(117);
        bill3.setBillType("HR");
        bill3.setBillNumber("9999");
        bill3.setTitle("Previous Congress Bill");
        bill3.setIntroducedDate("2022-05-10");

        legislationRepository.save(bill1);
        legislationRepository.save(bill2);
        legislationRepository.save(bill3);
    }

    // ============ findByCongressAndBillTypeAndBillNumber() Tests ============

    @Test
    @DisplayName("findByCongressAndBillTypeAndBillNumber returns bill when found")
    void testFindByCongressAndBillTypeAndBillNumberSuccess() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(118, "HR", "1234");

        assertTrue(result.isPresent());
        assertEquals("Test Bill 1", result.get().getTitle());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeAndBillNumber returns empty when not found")
    void testFindByCongressAndBillTypeAndBillNumberNotFound() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(118, "HR", "9999");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeAndBillNumber is case-sensitive for bill type")
    void testFindByCongressAndBillTypeAndBillNumberCaseSensitive() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(118, "hr", "1234");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeAndBillNumber distinguishes by congress")
    void testFindByCongressAndBillTypeAndBillNumberByCongress() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(117, "HR", "1234");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeAndBillNumber finds Senate bills")
    void testFindByCongressAndBillTypeAndBillNumberSenate() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(118, "S", "5678");

        assertTrue(result.isPresent());
        assertEquals("Test Senate Bill", result.get().getTitle());
    }

    // ============ findByCongressAndBillTypeIgnoreCaseAndBillNumber() Tests ============

    @Test
    @DisplayName("findByCongressAndBillTypeIgnoreCaseAndBillNumber returns bill when found")
    void testFindByCongressAndBillTypeIgnoreCaseAndBillNumberSuccess() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "HR", "1234");

        assertTrue(result.isPresent());
        assertEquals("Test Bill 1", result.get().getTitle());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeIgnoreCaseAndBillNumber ignores case in bill type")
    void testFindByCongressAndBillTypeIgnoreCaseAndBillNumberLowercase() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "hr", "1234");

        assertTrue(result.isPresent());
        assertEquals("Test Bill 1", result.get().getTitle());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeIgnoreCaseAndBillNumber handles uppercase")
    void testFindByCongressAndBillTypeIgnoreCaseAndBillNumberUppercase() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "HR", "1234");

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeIgnoreCaseAndBillNumber handles mixed case")
    void testFindByCongressAndBillTypeIgnoreCaseAndBillNumberMixedCase() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "Hr", "1234");

        assertTrue(result.isPresent());
        assertEquals("Test Bill 1", result.get().getTitle());
    }

    @Test
    @DisplayName("findByCongressAndBillTypeIgnoreCaseAndBillNumber returns empty when not found")
    void testFindByCongressAndBillTypeIgnoreCaseAndBillNumberNotFound() {
        Optional<LegislationEntity> result = legislationRepository
            .findByCongressAndBillTypeIgnoreCaseAndBillNumber(118, "HR", "9999");

        assertFalse(result.isPresent());
    }

    // ============ CRUD Operations ============

    @Test
    @DisplayName("save persists bill to database")
    void testSaveBill() {
        LegislationEntity newBill = new LegislationEntity();
        newBill.setCongress(118);
        newBill.setBillType("HR");
        newBill.setBillNumber("5555");
        newBill.setTitle("New Bill");

        legislationRepository.save(newBill);
        Optional<LegislationEntity> retrieved = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(118, "HR", "5555");

        assertTrue(retrieved.isPresent());
        assertEquals("New Bill", retrieved.get().getTitle());
    }

    @Test
    @DisplayName("save updates existing bill")
    void testUpdateBill() {
        bill1.setTitle("Updated Title");
        legislationRepository.save(bill1);

        Optional<LegislationEntity> retrieved = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(118, "HR", "1234");

        assertTrue(retrieved.isPresent());
        assertEquals("Updated Title", retrieved.get().getTitle());
    }

    @Test
    @DisplayName("delete removes bill from database")
    void testDeleteBill() {
        legislationRepository.delete(bill1);
        Optional<LegislationEntity> retrieved = legislationRepository
            .findByCongressAndBillTypeAndBillNumber(118, "HR", "1234");

        assertFalse(retrieved.isPresent());
    }

    @Test
    @DisplayName("findById returns bill when found")
    void testFindById() {
        Optional<LegislationEntity> result = legislationRepository.findById(bill1.getId());

        assertTrue(result.isPresent());
        assertEquals("Test Bill 1", result.get().getTitle());
    }

    @Test
    @DisplayName("findAll returns all bills")
    void testFindAll() {
        var result = legislationRepository.findAll();

        assertEquals(3, result.size());
    }
}
