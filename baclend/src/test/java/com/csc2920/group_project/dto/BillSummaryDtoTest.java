package com.csc2920.group_project.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BillSummaryDto Tests")
class BillSummaryDtoTest {

    @Test
    @DisplayName("Should create BillSummaryDto with all fields")
    void testCreateWithAllFields() {
        BillSummaryDto dto = new BillSummaryDto(
                "2024-01-15",
                "Passed Senate",
                "This bill establishes new guidelines",
                "2024-01-20",
                "v1.0"
        );

        assertEquals("2024-01-15", dto.actionDate());
        assertEquals("Passed Senate", dto.actionDesc());
        assertEquals("This bill establishes new guidelines", dto.text());
        assertEquals("2024-01-20", dto.updateDate());
        assertEquals("v1.0", dto.versionCode());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        BillSummaryDto dto = new BillSummaryDto(null, null, null, null, null);

        assertNull(dto.actionDate());
        assertNull(dto.actionDesc());
        assertNull(dto.text());
        assertNull(dto.updateDate());
        assertNull(dto.versionCode());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        BillSummaryDto dto = new BillSummaryDto("", "", "", "", "");

        assertEquals("", dto.actionDate());
        assertEquals("", dto.actionDesc());
        assertEquals("", dto.text());
        assertEquals("", dto.updateDate());
        assertEquals("", dto.versionCode());
    }

    @Test
    @DisplayName("Should handle long text")
    void testLongText() {
        String longText = "A".repeat(10000);
        BillSummaryDto dto = new BillSummaryDto("2024-01-15", "Action", longText, "2024-01-20", "v1");

        assertEquals(longText, dto.text());
        assertEquals(longText.length(), dto.text().length());
    }

    @Test
    @DisplayName("Should handle special characters")
    void testSpecialCharacters() {
        String specialText = "Bill with special chars: !@#$%^&*()_+-=[]{}|;:',.<>?/";
        BillSummaryDto dto = new BillSummaryDto("2024-01-15", specialText, specialText, "2024-01-20", "v1");

        assertEquals(specialText, dto.actionDesc());
        assertEquals(specialText, dto.text());
    }

    @Test
    @DisplayName("Should handle unicode characters")
    void testUnicodeCharacters() {
        String unicodeText = "Bill with unicode: α β γ δ ε ζ η θ 中文 日本語 한글";
        BillSummaryDto dto = new BillSummaryDto("2024-01-15", "Action", unicodeText, "2024-01-20", "v1");

        assertEquals(unicodeText, dto.text());
        assertTrue(dto.text().contains("中文"));
    }

    @Test
    @DisplayName("Should support value-based equality")
    void testEquality() {
        BillSummaryDto dto1 = new BillSummaryDto("2024-01-15", "Action", "Text", "2024-01-20", "v1");
        BillSummaryDto dto2 = new BillSummaryDto("2024-01-15", "Action", "Text", "2024-01-20", "v1");

        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should support inequality when fields differ")
    void testInequality() {
        BillSummaryDto dto1 = new BillSummaryDto("2024-01-15", "Action", "Text", "2024-01-20", "v1");
        BillSummaryDto dto2 = new BillSummaryDto("2024-01-16", "Action", "Text", "2024-01-20", "v1");

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should support hashCode consistency")
    void testHashCode() {
        BillSummaryDto dto1 = new BillSummaryDto("2024-01-15", "Action", "Text", "2024-01-20", "v1");
        BillSummaryDto dto2 = new BillSummaryDto("2024-01-15", "Action", "Text", "2024-01-20", "v1");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should provide meaningful toString()")
    void testToString() {
        BillSummaryDto dto = new BillSummaryDto("2024-01-15", "Action", "Text", "2024-01-20", "v1");
        String str = dto.toString();

        assertNotNull(str);
        assertTrue(str.contains("BillSummaryDto"));
        assertTrue(str.contains("2024-01-15"));
    }

    @Test
    @DisplayName("Should be immutable (record)")
    void testImmutability() {
        BillSummaryDto dto = new BillSummaryDto("2024-01-15", "Action", "Text", "2024-01-20", "v1");

        assertEquals("2024-01-15", dto.actionDate());
        assertEquals("Action", dto.actionDesc());
    }
}
