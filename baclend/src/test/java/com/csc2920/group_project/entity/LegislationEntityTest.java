package com.csc2920.group_project.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LegislationEntity Tests")
class LegislationEntityTest {

    private LegislationEntity legislation;

    @BeforeEach
    void setUp() {
        legislation = new LegislationEntity();
    }

    @Test
    @DisplayName("Should create LegislationEntity with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(legislation);
        assertNull(legislation.getId());
        assertNull(legislation.getCongress());
        assertNull(legislation.getBillType());
        assertNull(legislation.getBillNumber());
        assertNotNull(legislation.getMemberLinks());
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        legislation.setId(1L);
        legislation.setCongress(118);
        legislation.setBillType("hr");
        legislation.setBillNumber("1234");
        legislation.setIntroducedDate("2023-01-01");
        legislation.setTitle("An Act to Test");
        legislation.setPolicyArea("Government Operations");
        legislation.setLatestActionDate("2024-01-15");
        legislation.setLatestActionText("Referred to Committee");
        legislation.setUrl("https://congress.gov/bill/118/hr/1234");

        assertEquals(1L, legislation.getId());
        assertEquals(118, legislation.getCongress());
        assertEquals("hr", legislation.getBillType());
        assertEquals("1234", legislation.getBillNumber());
        assertEquals("2023-01-01", legislation.getIntroducedDate());
        assertEquals("An Act to Test", legislation.getTitle());
        assertEquals("Government Operations", legislation.getPolicyArea());
        assertEquals("2024-01-15", legislation.getLatestActionDate());
        assertEquals("Referred to Committee", legislation.getLatestActionText());
        assertEquals("https://congress.gov/bill/118/hr/1234", legislation.getUrl());
    }

    @Test
    @DisplayName("Should initialize memberLinks as empty HashSet")
    void testMemberLinksInitialization() {
        LegislationEntity newLeg = new LegislationEntity();
        assertNotNull(newLeg.getMemberLinks());
        assertTrue(newLeg.getMemberLinks().isEmpty());
        assertTrue(newLeg.getMemberLinks() instanceof Set);
    }

    @Test
    @DisplayName("Should support adding member links")
    void testAddMemberLink() {
        legislation.setId(1L);
        
        MemberLegislationEntity memLeg = new MemberLegislationEntity();
        memLeg.setId(10L);
        memLeg.setSource("sponsor");
        
        legislation.getMemberLinks().add(memLeg);
        
        assertEquals(1, legislation.getMemberLinks().size());
        assertTrue(legislation.getMemberLinks().contains(memLeg));
    }

    @Test
    @DisplayName("Should support removing member links")
    void testRemoveMemberLink() {
        legislation.setId(1L);
        
        MemberLegislationEntity memLeg = new MemberLegislationEntity();
        memLeg.setId(10L);
        
        legislation.getMemberLinks().add(memLeg);
        assertEquals(1, legislation.getMemberLinks().size());
        
        legislation.getMemberLinks().remove(memLeg);
        assertEquals(0, legislation.getMemberLinks().size());
    }

    @Test
    @DisplayName("Should support multiple member links")
    void testMultipleMemberLinks() {
        legislation.setId(1L);
        
        MemberLegislationEntity memLeg1 = new MemberLegislationEntity();
        memLeg1.setId(10L);
        memLeg1.setSource("sponsor");
        
        MemberLegislationEntity memLeg2 = new MemberLegislationEntity();
        memLeg2.setId(11L);
        memLeg2.setSource("cosponsor");
        
        legislation.getMemberLinks().add(memLeg1);
        legislation.getMemberLinks().add(memLeg2);
        
        assertEquals(2, legislation.getMemberLinks().size());
        assertTrue(legislation.getMemberLinks().contains(memLeg1));
        assertTrue(legislation.getMemberLinks().contains(memLeg2));
    }

    @Test
    @DisplayName("equals() should return true for same id")
    void testEqualsWithSameId() {
        LegislationEntity leg1 = new LegislationEntity();
        leg1.setId(1L);
        leg1.setBillNumber("1234");

        LegislationEntity leg2 = new LegislationEntity();
        leg2.setId(1L);
        leg2.setBillNumber("5678");

        assertEquals(leg1, leg2);
    }

    @Test
    @DisplayName("equals() should return false for different id")
    void testEqualsWithDifferentId() {
        LegislationEntity leg1 = new LegislationEntity();
        leg1.setId(1L);

        LegislationEntity leg2 = new LegislationEntity();
        leg2.setId(2L);

        assertNotEquals(leg1, leg2);
    }

    @Test
    @DisplayName("equals() should return true for same object reference")
    void testEqualsReflexive() {
        legislation.setId(1L);
        assertEquals(legislation, legislation);
    }

    @Test
    @DisplayName("equals() should return false when comparing with null")
    void testEqualsWithNull() {
        legislation.setId(1L);
        assertNotEquals(legislation, null);
    }

    @Test
    @DisplayName("equals() should return false when comparing with different type")
    void testEqualsWithDifferentType() {
        legislation.setId(1L);
        assertNotEquals(legislation, "1");
    }

    @Test
    @DisplayName("hashCode() should be consistent with equals()")
    void testHashCodeConsistency() {
        LegislationEntity leg1 = new LegislationEntity();
        leg1.setId(1L);

        LegislationEntity leg2 = new LegislationEntity();
        leg2.setId(1L);

        assertEquals(leg1, leg2);
        assertEquals(leg1.hashCode(), leg2.hashCode());
    }

    @Test
    @DisplayName("hashCode() should be consistent across multiple calls")
    void testHashCodeConsistency2() {
        legislation.setId(1L);
        int hashCode1 = legislation.hashCode();
        int hashCode2 = legislation.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should work correctly in HashSet")
    void testInHashSet() {
        LegislationEntity leg1 = new LegislationEntity();
        leg1.setId(1L);
        leg1.setBillNumber("1234");

        LegislationEntity leg2 = new LegislationEntity();
        leg2.setId(1L);
        leg2.setBillNumber("5678");

        Set<LegislationEntity> set = new HashSet<>();
        set.add(leg1);
        set.add(leg2);

        assertEquals(1, set.size());
        assertTrue(set.contains(leg1));
        assertTrue(set.contains(leg2));
    }

    @Test
    @DisplayName("Should handle null id in equals()")
    void testEqualsWithNullId() {
        LegislationEntity leg1 = new LegislationEntity();
        leg1.setId(null);

        LegislationEntity leg2 = new LegislationEntity();
        leg2.setId(null);

        assertEquals(leg1, leg2);
    }

    @Test
    @DisplayName("Should support null optional fields")
    void testOptionalFieldsNull() {
        legislation.setId(1L);
        legislation.setCongress(118);
        legislation.setBillType("hr");
        legislation.setBillNumber("1234");
        
        legislation.setIntroducedDate(null);
        legislation.setTitle(null);
        legislation.setPolicyArea(null);
        legislation.setLatestActionDate(null);
        legislation.setLatestActionText(null);
        legislation.setUrl(null);

        assertNull(legislation.getIntroducedDate());
        assertNull(legislation.getTitle());
        assertNull(legislation.getPolicyArea());
        assertNull(legislation.getLatestActionDate());
        assertNull(legislation.getLatestActionText());
        assertNull(legislation.getUrl());
    }

    @Test
    @DisplayName("memberLinks should be initialized as new HashSet")
    void testMemberLinksInstanceType() {
        LegislationEntity newLeg = new LegislationEntity();
        assertTrue(newLeg.getMemberLinks() instanceof HashSet);
    }

    @Test
    @DisplayName("Should support setting different congress values")
    void testDifferentCongressValues() {
        legislation.setId(1L);
        
        legislation.setCongress(116);
        assertEquals(116, legislation.getCongress());
        
        legislation.setCongress(117);
        assertEquals(117, legislation.getCongress());
        
        legislation.setCongress(118);
        assertEquals(118, legislation.getCongress());
    }

    @Test
    @DisplayName("Should support different bill types")
    void testDifferentBillTypes() {
        legislation.setId(1L);
        
        legislation.setBillType("hr");
        assertEquals("hr", legislation.getBillType());
        
        legislation.setBillType("s");
        assertEquals("s", legislation.getBillType());
        
        legislation.setBillType("hjres");
        assertEquals("hjres", legislation.getBillType());
    }
}
