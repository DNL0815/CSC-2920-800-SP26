package com.csc2920.group_project.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberDto Tests")
class MemberDtoTest {

    @Test
    @DisplayName("Should create MemberDto with all fields")
    void testCreateWithAllFields() {
        MemberDto.Depiction depiction = new MemberDto.Depiction();
        depiction.setAttribution("Official");
        depiction.setImageUrl("https://example.com/image.jpg");

        MemberDto.Term term = new MemberDto.Term();
        term.setStartYear(2023);
        term.setChamber("House");

        MemberDto.Terms terms = new MemberDto.Terms();
        terms.setItem(List.of(term));

        MemberDto dto = new MemberDto();
        dto.setBioguideId("A000001");
        dto.setName("John Smith");
        dto.setState("CA");
        dto.setDistrict(1);
        dto.setPartyName("Democratic");
        dto.setTerms(terms);
        dto.setDepiction(depiction);
        dto.setUpdateDate("2024-01-15");

        assertEquals("A000001", dto.getBioguideId());
        assertEquals("John Smith", dto.getName());
        assertEquals("CA", dto.getState());
        assertEquals(1, dto.getDistrict());
        assertEquals("Democratic", dto.getPartyName());
        assertNotNull(dto.getTerms());
        assertNotNull(dto.getDepiction());
        assertEquals("2024-01-15", dto.getUpdateDate());
    }

    @Test
    @DisplayName("Should handle null fields")
    void testNullFields() {
        MemberDto dto = new MemberDto();

        assertNull(dto.getBioguideId());
        assertNull(dto.getName());
        assertNull(dto.getState());
        assertNull(dto.getDistrict());
        assertNull(dto.getPartyName());
        assertNull(dto.getTerms());
        assertNull(dto.getDepiction());
        assertNull(dto.getUpdateDate());
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void testNoArgsConstructor() {
        MemberDto dto = new MemberDto();
        assertNotNull(dto);
    }

    @Test
    @DisplayName("Should handle various states")
    void testVariousStates() {
        MemberDto dto1 = new MemberDto();
        dto1.setState("CA");

        MemberDto dto2 = new MemberDto();
        dto2.setState("NY");

        MemberDto dto3 = new MemberDto();
        dto3.setState("TX");

        assertEquals("CA", dto1.getState());
        assertEquals("NY", dto2.getState());
        assertEquals("TX", dto3.getState());
    }

    @Test
    @DisplayName("Should handle various party names")
    void testVariousParties() {
        MemberDto demoDto = new MemberDto();
        demoDto.setPartyName("Democratic");

        MemberDto repDto = new MemberDto();
        repDto.setPartyName("Republican");

        MemberDto indDto = new MemberDto();
        indDto.setPartyName("Independent");

        assertEquals("Democratic", demoDto.getPartyName());
        assertEquals("Republican", repDto.getPartyName());
        assertEquals("Independent", indDto.getPartyName());
    }

    @Test
    @DisplayName("Should handle null and non-null districts")
    void testDistricts() {
        MemberDto dto1 = new MemberDto();
        dto1.setDistrict(1);
        assertEquals(1, dto1.getDistrict());

        MemberDto dto2 = new MemberDto();
        dto2.setDistrict(null);
        assertNull(dto2.getDistrict());

        MemberDto dto3 = new MemberDto();
        dto3.setDistrict(0);
        assertEquals(0, dto3.getDistrict());
    }

    @Test
    @DisplayName("Should handle long member names")
    void testLongNames() {
        String longName = "A".repeat(500);
        MemberDto dto = new MemberDto();
        dto.setName(longName);

        assertEquals(longName, dto.getName());
        assertEquals(500, dto.getName().length());
    }

    @Test
    @DisplayName("Should handle special characters in names")
    void testSpecialCharactersInName() {
        String specialName = "O'Brien-Smith Jr.";
        MemberDto dto = new MemberDto();
        dto.setName(specialName);

        assertEquals(specialName, dto.getName());
    }

    @Test
    @DisplayName("Should handle unicode in names")
    void testUnicodeInNames() {
        String unicodeName = "José García";
        MemberDto dto = new MemberDto();
        dto.setName(unicodeName);

        assertEquals(unicodeName, dto.getName());
    }

    @Test
    @DisplayName("Should support Lombok equality")
    void testEquality() {
        MemberDto dto1 = new MemberDto();
        dto1.setBioguideId("A000001");
        dto1.setName("John Smith");

        MemberDto dto2 = new MemberDto();
        dto2.setBioguideId("A000001");
        dto2.setName("John Smith");

        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should support inequality when bioguideId differs")
    void testInequalityByBioguideId() {
        MemberDto dto1 = new MemberDto();
        dto1.setBioguideId("A000001");

        MemberDto dto2 = new MemberDto();
        dto2.setBioguideId("B000001");

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should support Lombok hashCode")
    void testHashCode() {
        MemberDto dto1 = new MemberDto();
        dto1.setBioguideId("A000001");
        dto1.setName("John Smith");

        MemberDto dto2 = new MemberDto();
        dto2.setBioguideId("A000001");
        dto2.setName("John Smith");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should provide meaningful toString()")
    void testToString() {
        MemberDto dto = new MemberDto();
        dto.setBioguideId("A000001");
        dto.setName("John Smith");

        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("MemberDto"));
    }

    @Test
    @DisplayName("Depiction: Should create with all fields")
    void testDepictionWithFields() {
        MemberDto.Depiction depiction = new MemberDto.Depiction();
        depiction.setAttribution("Official");
        depiction.setImageUrl("https://example.com/image.jpg");

        assertEquals("Official", depiction.getAttribution());
        assertEquals("https://example.com/image.jpg", depiction.getImageUrl());
    }

    @Test
    @DisplayName("Depiction: Should handle null values")
    void testDepictionNullValues() {
        MemberDto.Depiction depiction = new MemberDto.Depiction();

        assertNull(depiction.getAttribution());
        assertNull(depiction.getImageUrl());
    }

    @Test
    @DisplayName("Depiction: Should support equality")
    void testDepictionEquality() {
        MemberDto.Depiction dep1 = new MemberDto.Depiction();
        dep1.setAttribution("Official");
        dep1.setImageUrl("https://example.com/image.jpg");

        MemberDto.Depiction dep2 = new MemberDto.Depiction();
        dep2.setAttribution("Official");
        dep2.setImageUrl("https://example.com/image.jpg");

        assertEquals(dep1, dep2);
    }

    @Test
    @DisplayName("Term: Should create with all fields")
    void testTermWithFields() {
        MemberDto.Term term = new MemberDto.Term();
        term.setStartYear(2023);
        term.setChamber("House");

        assertEquals(2023, term.getStartYear());
        assertEquals("House", term.getChamber());
    }

    @Test
    @DisplayName("Term: Should handle null values")
    void testTermNullValues() {
        MemberDto.Term term = new MemberDto.Term();

        assertNull(term.getStartYear());
        assertNull(term.getChamber());
    }

    @Test
    @DisplayName("Term: Should handle both chambers")
    void testTermChambers() {
        MemberDto.Term house = new MemberDto.Term();
        house.setChamber("House");

        MemberDto.Term senate = new MemberDto.Term();
        senate.setChamber("Senate");

        assertEquals("House", house.getChamber());
        assertEquals("Senate", senate.getChamber());
    }

    @Test
    @DisplayName("Term: Should support equality")
    void testTermEquality() {
        MemberDto.Term term1 = new MemberDto.Term();
        term1.setStartYear(2023);
        term1.setChamber("House");

        MemberDto.Term term2 = new MemberDto.Term();
        term2.setStartYear(2023);
        term2.setChamber("House");

        assertEquals(term1, term2);
    }

    @Test
    @DisplayName("Terms: Should create with term list")
    void testTermsWithList() {
        MemberDto.Term term1 = new MemberDto.Term();
        term1.setStartYear(2023);
        term1.setChamber("House");

        MemberDto.Term term2 = new MemberDto.Term();
        term2.setStartYear(2021);
        term2.setChamber("House");

        MemberDto.Terms terms = new MemberDto.Terms();
        terms.setItem(List.of(term1, term2));

        assertNotNull(terms.getItem());
        assertEquals(2, terms.getItem().size());
    }

    @Test
    @DisplayName("Terms: Should handle null list")
    void testTermsNullList() {
        MemberDto.Terms terms = new MemberDto.Terms();

        assertNull(terms.getItem());
    }

    @Test
    @DisplayName("Terms: Should handle empty list")
    void testTermsEmptyList() {
        MemberDto.Terms terms = new MemberDto.Terms();
        terms.setItem(new ArrayList<>());

        assertNotNull(terms.getItem());
        assertEquals(0, terms.getItem().size());
    }

    @Test
    @DisplayName("Terms: Should support equality")
    void testTermsEquality() {
        MemberDto.Term term = new MemberDto.Term();
        term.setStartYear(2023);
        term.setChamber("House");

        MemberDto.Terms terms1 = new MemberDto.Terms();
        terms1.setItem(List.of(term));

        MemberDto.Terms terms2 = new MemberDto.Terms();
        terms2.setItem(List.of(term));

        assertEquals(terms1, terms2);
    }
}
