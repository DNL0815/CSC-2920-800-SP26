package com.csc2920.group_project.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberEntity Tests")
class MemberEntityTest {

    private MemberEntity member;

    @BeforeEach
    void setUp() {
        member = new MemberEntity();
    }

    @Test
    @DisplayName("Should create MemberEntity with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(member);
        assertNull(member.getBioguideId());
        assertNull(member.getName());
        assertNull(member.getCurrentMember());
        assertNotNull(member.getMemberLegislation());
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        member.setBioguideId("B001");
        member.setName("John Doe");
        member.setCurrentMember(true);
        member.setPartyName("D");
        member.setState("CA");
        member.setDistrict(1);
        member.setChamber("House");
        member.setStartYear(2020);
        member.setImageUrl("https://example.com/image.jpg");
        member.setUpdateDate("2024-01-01");

        assertEquals("B001", member.getBioguideId());
        assertEquals("John Doe", member.getName());
        assertTrue(member.getCurrentMember());
        assertEquals("D", member.getPartyName());
        assertEquals("CA", member.getState());
        assertEquals(1, member.getDistrict());
        assertEquals("House", member.getChamber());
        assertEquals(2020, member.getStartYear());
        assertEquals("https://example.com/image.jpg", member.getImageUrl());
        assertEquals("2024-01-01", member.getUpdateDate());
    }

    @Test
    @DisplayName("Should initialize memberLegislation as empty HashSet")
    void testMemberLegislationInitialization() {
        MemberEntity newMember = new MemberEntity();
        assertNotNull(newMember.getMemberLegislation());
        assertTrue(newMember.getMemberLegislation().isEmpty());
        assertTrue(newMember.getMemberLegislation() instanceof Set);
    }

    @Test
    @DisplayName("Should support adding member legislation")
    void testAddMemberLegislation() {
        member.setBioguideId("B001");
        
        MemberLegislationEntity memLeg1 = new MemberLegislationEntity();
        memLeg1.setId(1L);
        memLeg1.setSource("sponsor");
        
        member.getMemberLegislation().add(memLeg1);
        
        assertEquals(1, member.getMemberLegislation().size());
        assertTrue(member.getMemberLegislation().contains(memLeg1));
    }

    @Test
    @DisplayName("Should support removing member legislation")
    void testRemoveMemberLegislation() {
        member.setBioguideId("B001");
        
        MemberLegislationEntity memLeg = new MemberLegislationEntity();
        memLeg.setId(1L);
        
        member.getMemberLegislation().add(memLeg);
        assertEquals(1, member.getMemberLegislation().size());
        
        member.getMemberLegislation().remove(memLeg);
        assertEquals(0, member.getMemberLegislation().size());
    }

    @Test
    @DisplayName("equals() should return true for same bioguideId")
    void testEqualsWithSameBioguideId() {
        MemberEntity member1 = new MemberEntity();
        member1.setBioguideId("B001");
        member1.setName("John Doe");

        MemberEntity member2 = new MemberEntity();
        member2.setBioguideId("B001");
        member2.setName("Jane Doe");

        assertEquals(member1, member2);
    }

    @Test
    @DisplayName("equals() should return false for different bioguideId")
    void testEqualsWithDifferentBioguideId() {
        MemberEntity member1 = new MemberEntity();
        member1.setBioguideId("B001");

        MemberEntity member2 = new MemberEntity();
        member2.setBioguideId("B002");

        assertNotEquals(member1, member2);
    }

    @Test
    @DisplayName("equals() should return true for same object reference")
    void testEqualsReflexive() {
        member.setBioguideId("B001");
        assertEquals(member, member);
    }

    @Test
    @DisplayName("equals() should return false when comparing with null")
    void testEqualsWithNull() {
        member.setBioguideId("B001");
        assertNotEquals(member, null);
    }

    @Test
    @DisplayName("equals() should return false when comparing with different type")
    void testEqualsWithDifferentType() {
        member.setBioguideId("B001");
        assertNotEquals(member, "B001");
    }

    @Test
    @DisplayName("hashCode() should be consistent with equals()")
    void testHashCodeConsistency() {
        MemberEntity member1 = new MemberEntity();
        member1.setBioguideId("B001");

        MemberEntity member2 = new MemberEntity();
        member2.setBioguideId("B001");

        assertEquals(member1, member2);
        assertEquals(member1.hashCode(), member2.hashCode());
    }

    @Test
    @DisplayName("hashCode() should be consistent across multiple calls")
    void testHashCodeConsistency2() {
        member.setBioguideId("B001");
        int hashCode1 = member.hashCode();
        int hashCode2 = member.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should work correctly in HashSet")
    void testInHashSet() {
        MemberEntity member1 = new MemberEntity();
        member1.setBioguideId("B001");
        member1.setName("John Doe");

        MemberEntity member2 = new MemberEntity();
        member2.setBioguideId("B001");
        member2.setName("Jane Doe");

        Set<MemberEntity> set = new HashSet<>();
        set.add(member1);
        set.add(member2);

        assertEquals(1, set.size());
        assertTrue(set.contains(member1));
        assertTrue(set.contains(member2));
    }

    @Test
    @DisplayName("Should handle null bioguideId in equals()")
    void testEqualsWithNullBioguideId() {
        MemberEntity member1 = new MemberEntity();
        member1.setBioguideId(null);

        MemberEntity member2 = new MemberEntity();
        member2.setBioguideId(null);

        assertEquals(member1, member2);
    }

    @Test
    @DisplayName("Should handle false currentMember flag")
    void testCurrentMemberFalse() {
        member.setBioguideId("B001");
        member.setCurrentMember(false);
        assertFalse(member.getCurrentMember());
    }

    @Test
    @DisplayName("Should handle null currentMember flag")
    void testCurrentMemberNull() {
        member.setBioguideId("B001");
        member.setCurrentMember(null);
        assertNull(member.getCurrentMember());
    }

    @Test
    @DisplayName("Should support null optional fields")
    void testOptionalFieldsNull() {
        member.setBioguideId("B001");
        member.setName("John Doe");
        member.setDistrict(null);
        member.setImageUrl(null);
        member.setUpdateDate(null);

        assertNull(member.getDistrict());
        assertNull(member.getImageUrl());
        assertNull(member.getUpdateDate());
    }

    @Test
    @DisplayName("memberLegislation should be initialized as new HashSet")
    void testMemberLegislationInstanceType() {
        MemberEntity newMember = new MemberEntity();
        assertTrue(newMember.getMemberLegislation() instanceof HashSet);
    }
}
