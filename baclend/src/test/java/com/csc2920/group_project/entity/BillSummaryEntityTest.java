package com.csc2920.group_project.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BillSummaryEntity Tests")
class BillSummaryEntityTest {

    private BillSummaryEntity billSummary;
    private LegislationEntity legislation;

    @BeforeEach
    void setUp() {
        billSummary = new BillSummaryEntity();
        legislation = new LegislationEntity();
        legislation.setId(1L);
    }

    @Test
    @DisplayName("Should create BillSummaryEntity with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(billSummary);
        assertNull(billSummary.getId());
        assertNull(billSummary.getLegislation());
        assertNull(billSummary.getActionDate());
        assertNull(billSummary.getActionDesc());
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        billSummary.setId(1L);
        billSummary.setLegislation(legislation);
        billSummary.setActionDate("2023-01-15");
        billSummary.setActionDesc("Summary action description");
        billSummary.setText("This is a long bill summary text");
        billSummary.setUpdateDate("2024-01-15");
        billSummary.setVersionCode("00c");

        assertEquals(1L, billSummary.getId());
        assertEquals(legislation, billSummary.getLegislation());
        assertEquals("2023-01-15", billSummary.getActionDate());
        assertEquals("Summary action description", billSummary.getActionDesc());
        assertEquals("This is a long bill summary text", billSummary.getText());
        assertEquals("2024-01-15", billSummary.getUpdateDate());
        assertEquals("00c", billSummary.getVersionCode());
    }

    @Test
    @DisplayName("Should support legislation relationship")
    void testLegislationRelationship() {
        LegislationEntity leg1 = new LegislationEntity();
        leg1.setId(1L);
        leg1.setBillNumber("1234");

        LegislationEntity leg2 = new LegislationEntity();
        leg2.setId(2L);
        leg2.setBillNumber("5678");

        billSummary.setLegislation(leg1);
        assertEquals(leg1, billSummary.getLegislation());

        billSummary.setLegislation(leg2);
        assertEquals(leg2, billSummary.getLegislation());
    }

    @Test
    @DisplayName("Should support null legislation")
    void testNullLegislation() {
        billSummary.setLegislation(null);
        assertNull(billSummary.getLegislation());
    }

    @Test
    @DisplayName("equals() should return true for same id")
    void testEqualsWithSameId() {
        BillSummaryEntity summary1 = new BillSummaryEntity();
        summary1.setId(1L);
        summary1.setVersionCode("00c");

        BillSummaryEntity summary2 = new BillSummaryEntity();
        summary2.setId(1L);
        summary2.setVersionCode("00d");

        assertEquals(summary1, summary2);
    }

    @Test
    @DisplayName("equals() should return false for different id")
    void testEqualsWithDifferentId() {
        BillSummaryEntity summary1 = new BillSummaryEntity();
        summary1.setId(1L);

        BillSummaryEntity summary2 = new BillSummaryEntity();
        summary2.setId(2L);

        assertNotEquals(summary1, summary2);
    }

    @Test
    @DisplayName("equals() should return true for same object reference")
    void testEqualsReflexive() {
        billSummary.setId(1L);
        assertEquals(billSummary, billSummary);
    }

    @Test
    @DisplayName("equals() should return false when comparing with null")
    void testEqualsWithNull() {
        billSummary.setId(1L);
        assertNotEquals(billSummary, null);
    }

    @Test
    @DisplayName("equals() should return false when comparing with different type")
    void testEqualsWithDifferentType() {
        billSummary.setId(1L);
        assertNotEquals(billSummary, "1");
    }

    @Test
    @DisplayName("hashCode() should be consistent with equals()")
    void testHashCodeConsistency() {
        BillSummaryEntity summary1 = new BillSummaryEntity();
        summary1.setId(1L);

        BillSummaryEntity summary2 = new BillSummaryEntity();
        summary2.setId(1L);

        assertEquals(summary1, summary2);
        assertEquals(summary1.hashCode(), summary2.hashCode());
    }

    @Test
    @DisplayName("hashCode() should be consistent across multiple calls")
    void testHashCodeConsistency2() {
        billSummary.setId(1L);
        int hashCode1 = billSummary.hashCode();
        int hashCode2 = billSummary.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should handle null id in equals()")
    void testEqualsWithNullId() {
        BillSummaryEntity summary1 = new BillSummaryEntity();
        summary1.setId(null);

        BillSummaryEntity summary2 = new BillSummaryEntity();
        summary2.setId(null);

        assertEquals(summary1, summary2);
    }

    @Test
    @DisplayName("Should support null optional fields")
    void testOptionalFieldsNull() {
        billSummary.setId(1L);
        billSummary.setLegislation(legislation);
        
        billSummary.setActionDate(null);
        billSummary.setActionDesc(null);
        billSummary.setText(null);
        billSummary.setUpdateDate(null);
        billSummary.setVersionCode(null);

        assertNull(billSummary.getActionDate());
        assertNull(billSummary.getActionDesc());
        assertNull(billSummary.getText());
        assertNull(billSummary.getUpdateDate());
        assertNull(billSummary.getVersionCode());
    }

    @Test
    @DisplayName("Should handle long text content")
    void testLongTextContent() {
        String longText = "Lorem ipsum dolor sit amet, ".repeat(100);
        billSummary.setId(1L);
        billSummary.setText(longText);
        
        assertEquals(longText, billSummary.getText());
    }

    @Test
    @DisplayName("Should support different version codes")
    void testDifferentVersionCodes() {
        billSummary.setId(1L);
        
        billSummary.setVersionCode("00c");
        assertEquals("00c", billSummary.getVersionCode());
        
        billSummary.setVersionCode("00d");
        assertEquals("00d", billSummary.getVersionCode());
        
        billSummary.setVersionCode("06a");
        assertEquals("06a", billSummary.getVersionCode());
    }

    @Test
    @DisplayName("Should support updating all properties")
    void testUpdateAllProperties() {
        BillSummaryEntity summary = new BillSummaryEntity();
        
        summary.setId(1L);
        LegislationEntity leg = new LegislationEntity();
        leg.setId(100L);
        summary.setLegislation(leg);
        summary.setActionDate("2023-01-01");
        summary.setActionDesc("Original action");
        summary.setText("Original text");
        summary.setUpdateDate("2023-01-01");
        summary.setVersionCode("00c");
        
        assertEquals(1L, summary.getId());
        assertEquals(100L, summary.getLegislation().getId());
        
        // Update all properties
        summary.setActionDate("2024-01-01");
        summary.setActionDesc("Updated action");
        summary.setText("Updated text");
        summary.setUpdateDate("2024-01-01");
        summary.setVersionCode("00d");
        
        assertEquals("2024-01-01", summary.getActionDate());
        assertEquals("Updated action", summary.getActionDesc());
        assertEquals("Updated text", summary.getText());
        assertEquals("2024-01-01", summary.getUpdateDate());
        assertEquals("00d", summary.getVersionCode());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        billSummary.setId(1L);
        billSummary.setActionDate("");
        billSummary.setActionDesc("");
        billSummary.setText("");
        billSummary.setUpdateDate("");
        billSummary.setVersionCode("");
        
        assertEquals("", billSummary.getActionDate());
        assertEquals("", billSummary.getActionDesc());
        assertEquals("", billSummary.getText());
        assertEquals("", billSummary.getUpdateDate());
        assertEquals("", billSummary.getVersionCode());
    }
}
