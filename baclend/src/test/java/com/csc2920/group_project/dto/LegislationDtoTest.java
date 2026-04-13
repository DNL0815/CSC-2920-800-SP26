package com.csc2920.group_project.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LegislationDto Tests")
class LegislationDtoTest {

    @Test
    @DisplayName("Should create LegislationDto with all fields")
    void testCreateWithAllFields() {
        LegislationDto dto = new LegislationDto(
                118,
                "HR",
                "1234",
                "2023-01-15",
                "Healthcare Reform Act",
                "Health",
                "2023-06-20",
                "Passed Senate",
                "https://congress.gov/bill/118/hr/1234",
                "congress.gov"
        );

        assertEquals(118, dto.congress());
        assertEquals("HR", dto.billType());
        assertEquals("1234", dto.billNumber());
        assertEquals("2023-01-15", dto.introducedDate());
        assertEquals("Healthcare Reform Act", dto.title());
        assertEquals("Health", dto.policyArea());
        assertEquals("2023-06-20", dto.latestActionDate());
        assertEquals("Passed Senate", dto.latestActionText());
        assertEquals("https://congress.gov/bill/118/hr/1234", dto.url());
        assertEquals("congress.gov", dto.source());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        LegislationDto dto = new LegislationDto(null, null, null, null, null, null, null, null, null, null);

        assertNull(dto.congress());
        assertNull(dto.billType());
        assertNull(dto.billNumber());
        assertNull(dto.introducedDate());
        assertNull(dto.title());
        assertNull(dto.policyArea());
        assertNull(dto.latestActionDate());
        assertNull(dto.latestActionText());
        assertNull(dto.url());
        assertNull(dto.source());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        LegislationDto dto = new LegislationDto(118, "", "", "", "", "", "", "", "", "");

        assertEquals("", dto.billType());
        assertEquals("", dto.billNumber());
        assertEquals("", dto.title());
    }

    @Test
    @DisplayName("Should handle various congress numbers")
    void testVariousCongressNumbers() {
        LegislationDto dto1 = new LegislationDto(100, "HR", "1", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto dto2 = new LegislationDto(118, "HR", "1", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto dto3 = new LegislationDto(1, "HR", "1", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");

        assertEquals(100, dto1.congress());
        assertEquals(118, dto2.congress());
        assertEquals(1, dto3.congress());
    }

    @Test
    @DisplayName("Should handle various bill types")
    void testVariousBillTypes() {
        LegislationDto hr = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto senate = new LegislationDto(118, "S", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto hjres = new LegislationDto(118, "HJRES", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");

        assertEquals("HR", hr.billType());
        assertEquals("S", senate.billType());
        assertEquals("HJRES", hjres.billType());
    }

    @Test
    @DisplayName("Should handle long title")
    void testLongTitle() {
        String longTitle = "A".repeat(500);
        LegislationDto dto = new LegislationDto(118, "HR", "1234", "2023-01-15", longTitle, "Area", "2023-06-20", "Action", "url", "source");

        assertEquals(longTitle, dto.title());
        assertEquals(500, dto.title().length());
    }

    @Test
    @DisplayName("Should handle special characters in fields")
    void testSpecialCharacters() {
        String specialTitle = "Bill with special chars: !@#$%^&*()_+-=[]{}|;:',.<>?/";
        LegislationDto dto = new LegislationDto(
                118,
                "HR",
                "1234",
                "2023-01-15",
                specialTitle,
                specialTitle,
                "2023-06-20",
                specialTitle,
                "https://congress.gov/special?param=value&other=123",
                specialTitle
        );

        assertEquals(specialTitle, dto.title());
        assertTrue(dto.url().contains("?"));
        assertTrue(dto.url().contains("&"));
    }

    @Test
    @DisplayName("Should handle unicode in policy area")
    void testUnicodeCharacters() {
        String unicodeArea = "Health International Affairs Education Area";
        LegislationDto dto = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", unicodeArea, "2023-06-20", "Action", "url", "source");

        assertEquals(unicodeArea, dto.policyArea());
        assertTrue(dto.policyArea().contains("International"));
    }

    @Test
    @DisplayName("Should support value-based equality")
    void testEquality() {
        LegislationDto dto1 = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto dto2 = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");

        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should support inequality when congress differs")
    void testInequalityByCongressNumber() {
        LegislationDto dto1 = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto dto2 = new LegislationDto(117, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should support inequality when bill type differs")
    void testInequalityByBillType() {
        LegislationDto dto1 = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto dto2 = new LegislationDto(118, "S", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should support hashCode consistency")
    void testHashCode() {
        LegislationDto dto1 = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        LegislationDto dto2 = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should provide meaningful toString()")
    void testToString() {
        LegislationDto dto = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");
        String str = dto.toString();

        assertNotNull(str);
        assertTrue(str.contains("LegislationDto"));
        assertTrue(str.contains("118"));
        assertTrue(str.contains("HR"));
    }

    @Test
    @DisplayName("Should be immutable (record)")
    void testImmutability() {
        LegislationDto dto = new LegislationDto(118, "HR", "1234", "2023-01-15", "Title", "Area", "2023-06-20", "Action", "url", "source");

        assertEquals(118, dto.congress());
        assertEquals("HR", dto.billType());
        assertEquals("1234", dto.billNumber());
    }
}
