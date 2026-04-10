package com.csc2920.group_project.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberLegislationEntity Tests")
class MemberLegislationEntityTest {

    private MemberLegislationEntity memberLegislation;
    private MemberEntity member;
    private LegislationEntity legislation;

    @BeforeEach
    void setUp() {
        memberLegislation = new MemberLegislationEntity();
        
        member = new MemberEntity();
        member.setBioguideId("B001");
        member.setName("John Doe");
        
        legislation = new LegislationEntity();
        legislation.setId(1L);
        legislation.setBillNumber("1234");
    }

    @Test
    @DisplayName("Should create MemberLegislationEntity with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(memberLegislation);
        assertNull(memberLegislation.getId());
        assertNull(memberLegislation.getMember());
        assertNull(memberLegislation.getLegislation());
        assertNull(memberLegislation.getSource());
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        memberLegislation.setId(1L);
        memberLegislation.setMember(member);
        memberLegislation.setLegislation(legislation);
        memberLegislation.setSource("sponsor");

        assertEquals(1L, memberLegislation.getId());
        assertEquals(member, memberLegislation.getMember());
        assertEquals(legislation, memberLegislation.getLegislation());
        assertEquals("sponsor", memberLegislation.getSource());
    }

    @Test
    @DisplayName("Should support member relationship")
    void testMemberRelationship() {
        MemberEntity member1 = new MemberEntity();
        member1.setBioguideId("B001");

        MemberEntity member2 = new MemberEntity();
        member2.setBioguideId("B002");

        memberLegislation.setMember(member1);
        assertEquals(member1, memberLegislation.getMember());

        memberLegislation.setMember(member2);
        assertEquals(member2, memberLegislation.getMember());
    }

    @Test
    @DisplayName("Should support legislation relationship")
    void testLegislationRelationship() {
        LegislationEntity leg1 = new LegislationEntity();
        leg1.setId(1L);

        LegislationEntity leg2 = new LegislationEntity();
        leg2.setId(2L);

        memberLegislation.setLegislation(leg1);
        assertEquals(leg1, memberLegislation.getLegislation());

        memberLegislation.setLegislation(leg2);
        assertEquals(leg2, memberLegislation.getLegislation());
    }

    @Test
    @DisplayName("Should support different source types")
    void testDifferentSourceTypes() {
        memberLegislation.setId(1L);
        
        memberLegislation.setSource("sponsor");
        assertEquals("sponsor", memberLegislation.getSource());
        
        memberLegislation.setSource("cosponsor");
        assertEquals("cosponsor", memberLegislation.getSource());
        
        memberLegislation.setSource("actor");
        assertEquals("actor", memberLegislation.getSource());
    }

    @Test
    @DisplayName("Should support null member")
    void testNullMember() {
        memberLegislation.setMember(null);
        assertNull(memberLegislation.getMember());
    }

    @Test
    @DisplayName("Should support null legislation")
    void testNullLegislation() {
        memberLegislation.setLegislation(null);
        assertNull(memberLegislation.getLegislation());
    }

    @Test
    @DisplayName("Should support null source")
    void testNullSource() {
        memberLegislation.setSource(null);
        assertNull(memberLegislation.getSource());
    }

    @Test
    @DisplayName("equals() should return true for same id")
    void testEqualsWithSameId() {
        MemberLegislationEntity memLeg1 = new MemberLegislationEntity();
        memLeg1.setId(1L);
        memLeg1.setSource("sponsor");

        MemberLegislationEntity memLeg2 = new MemberLegislationEntity();
        memLeg2.setId(1L);
        memLeg2.setSource("cosponsor");

        assertEquals(memLeg1, memLeg2);
    }

    @Test
    @DisplayName("equals() should return false for different id")
    void testEqualsWithDifferentId() {
        MemberLegislationEntity memLeg1 = new MemberLegislationEntity();
        memLeg1.setId(1L);

        MemberLegislationEntity memLeg2 = new MemberLegislationEntity();
        memLeg2.setId(2L);

        assertNotEquals(memLeg1, memLeg2);
    }

    @Test
    @DisplayName("equals() should return true for same object reference")
    void testEqualsReflexive() {
        memberLegislation.setId(1L);
        assertEquals(memberLegislation, memberLegislation);
    }

    @Test
    @DisplayName("equals() should return false when comparing with null")
    void testEqualsWithNull() {
        memberLegislation.setId(1L);
        assertNotEquals(memberLegislation, null);
    }

    @Test
    @DisplayName("equals() should return false when comparing with different type")
    void testEqualsWithDifferentType() {
        memberLegislation.setId(1L);
        assertNotEquals(memberLegislation, "1");
    }

    @Test
    @DisplayName("hashCode() should be consistent with equals()")
    void testHashCodeConsistency() {
        MemberLegislationEntity memLeg1 = new MemberLegislationEntity();
        memLeg1.setId(1L);

        MemberLegislationEntity memLeg2 = new MemberLegislationEntity();
        memLeg2.setId(1L);

        assertEquals(memLeg1, memLeg2);
        assertEquals(memLeg1.hashCode(), memLeg2.hashCode());
    }

    @Test
    @DisplayName("hashCode() should be consistent across multiple calls")
    void testHashCodeConsistency2() {
        memberLegislation.setId(1L);
        int hashCode1 = memberLegislation.hashCode();
        int hashCode2 = memberLegislation.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should handle null id in equals()")
    void testEqualsWithNullId() {
        MemberLegislationEntity memLeg1 = new MemberLegislationEntity();
        memLeg1.setId(null);

        MemberLegislationEntity memLeg2 = new MemberLegislationEntity();
        memLeg2.setId(null);

        assertEquals(memLeg1, memLeg2);
    }

    @Test
    @DisplayName("Should represent a junction between member and legislation")
    void testJunctionTableRepresentation() {
        MemberEntity mem = new MemberEntity();
        mem.setBioguideId("B001");
        mem.setName("John Doe");

        LegislationEntity leg = new LegislationEntity();
        leg.setId(1L);
        leg.setBillNumber("1234");

        memberLegislation.setId(1L);
        memberLegislation.setMember(mem);
        memberLegislation.setLegislation(leg);
        memberLegislation.setSource("sponsor");

        assertNotNull(memberLegislation.getMember());
        assertNotNull(memberLegislation.getLegislation());
        assertEquals("sponsor", memberLegislation.getSource());
    }

    @Test
    @DisplayName("Should support complete lifecycle - create, read, update")
    void testCompleteLifecycle() {
        // Create
        MemberLegislationEntity memLeg = new MemberLegislationEntity();
        memLeg.setId(1L);
        memLeg.setMember(member);
        memLeg.setLegislation(legislation);
        memLeg.setSource("sponsor");
        
        // Verify creation
        assertEquals(1L, memLeg.getId());
        assertEquals("sponsor", memLeg.getSource());
        
        // Update
        memLeg.setSource("cosponsor");
        assertEquals("cosponsor", memLeg.getSource());
        
        // Verify member and legislation still intact
        assertNotNull(memLeg.getMember());
        assertNotNull(memLeg.getLegislation());
    }

    @Test
    @DisplayName("Should allow clearing relationships")
    void testClearingRelationships() {
        memberLegislation.setId(1L);
        memberLegislation.setMember(member);
        memberLegislation.setLegislation(legislation);
        memberLegislation.setSource("sponsor");
        
        // Verify relationships are set
        assertNotNull(memberLegislation.getMember());
        assertNotNull(memberLegislation.getLegislation());
        
        // Clear relationships
        memberLegislation.setMember(null);
        memberLegislation.setLegislation(null);
        memberLegislation.setSource(null);
        
        // Verify relationships are cleared
        assertNull(memberLegislation.getMember());
        assertNull(memberLegislation.getLegislation());
        assertNull(memberLegislation.getSource());
    }

    @Test
    @DisplayName("Should support empty source string")
    void testEmptySourceString() {
        memberLegislation.setId(1L);
        memberLegislation.setSource("");
        assertEquals("", memberLegislation.getSource());
    }

    @Test
    @DisplayName("Should support multiple instances with different sources for same member/legislation")
    void testMultipleSourcesForMemberAndLegislation() {
        MemberLegislationEntity sponsor = new MemberLegislationEntity();
        sponsor.setId(1L);
        sponsor.setMember(member);
        sponsor.setLegislation(legislation);
        sponsor.setSource("sponsor");
        
        MemberLegislationEntity cosponsor = new MemberLegislationEntity();
        cosponsor.setId(2L);
        cosponsor.setMember(member);
        cosponsor.setLegislation(legislation);
        cosponsor.setSource("cosponsor");
        
        assertNotEquals(sponsor, cosponsor);
        assertEquals(member, sponsor.getMember());
        assertEquals(member, cosponsor.getMember());
        assertEquals(legislation, sponsor.getLegislation());
        assertEquals(legislation, cosponsor.getLegislation());
        assertNotEquals(sponsor.getSource(), cosponsor.getSource());
    }
}
