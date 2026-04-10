package com.csc2920.group_project.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemberResponse Tests")
class MemberResponseTest {

    @Test
    @DisplayName("Should create MemberResponse with member list")
    void testCreateWithMembers() {
        MemberDto member1 = new MemberDto();
        member1.setBioguideId("A000001");
        member1.setName("John Smith");

        MemberDto member2 = new MemberDto();
        member2.setBioguideId("B000001");
        member2.setName("Jane Doe");

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(member1, member2));

        assertNotNull(response.getMembers());
        assertEquals(2, response.getMembers().size());
        assertEquals("John Smith", response.getMembers().get(0).getName());
        assertEquals("Jane Doe", response.getMembers().get(1).getName());
    }

    @Test
    @DisplayName("Should handle null member list")
    void testNullMemberList() {
        MemberResponse response = new MemberResponse();

        assertNull(response.getMembers());
    }

    @Test
    @DisplayName("Should handle empty member list")
    void testEmptyMemberList() {
        MemberResponse response = new MemberResponse();
        response.setMembers(new ArrayList<>());

        assertNotNull(response.getMembers());
        assertEquals(0, response.getMembers().size());
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void testNoArgsConstructor() {
        MemberResponse response = new MemberResponse();
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should handle large member list")
    void testLargeMemberList() {
        List<MemberDto> members = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            MemberDto member = new MemberDto();
            member.setBioguideId("ID" + i);
            member.setName("Member " + i);
            members.add(member);
        }

        MemberResponse response = new MemberResponse();
        response.setMembers(members);

        assertEquals(1000, response.getMembers().size());
        assertEquals("Member 0", response.getMembers().get(0).getName());
        assertEquals("Member 999", response.getMembers().get(999).getName());
    }

    @Test
    @DisplayName("Should handle members with null fields")
    void testMembersWithNullFields() {
        MemberDto member1 = new MemberDto();
        member1.setBioguideId("A000001");
        // Name not set (null)

        MemberDto member2 = new MemberDto();
        member2.setName("Jane Doe");
        // BioguideId not set (null)

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(member1, member2));

        assertEquals(2, response.getMembers().size());
        assertNull(response.getMembers().get(0).getName());
        assertNull(response.getMembers().get(1).getBioguideId());
    }

    @Test
    @DisplayName("Should handle members with all fields populated")
    void testMembersWithAllFields() {
        MemberDto.Depiction depiction = new MemberDto.Depiction();
        depiction.setAttribution("Official");
        depiction.setImageUrl("https://example.com/image.jpg");

        MemberDto.Term term = new MemberDto.Term();
        term.setStartYear(2023);
        term.setChamber("House");

        MemberDto.Terms terms = new MemberDto.Terms();
        terms.setItem(List.of(term));

        MemberDto member = new MemberDto();
        member.setBioguideId("A000001");
        member.setName("John Smith");
        member.setState("CA");
        member.setDistrict(1);
        member.setPartyName("Democratic");
        member.setTerms(terms);
        member.setDepiction(depiction);
        member.setUpdateDate("2024-01-15");

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(member));

        assertEquals(1, response.getMembers().size());
        MemberDto retrieved = response.getMembers().get(0);
        assertEquals("A000001", retrieved.getBioguideId());
        assertEquals("John Smith", retrieved.getName());
        assertEquals("CA", retrieved.getState());
        assertEquals(1, retrieved.getDistrict());
        assertEquals("Democratic", retrieved.getPartyName());
        assertNotNull(retrieved.getTerms());
        assertNotNull(retrieved.getDepiction());
        assertEquals("2024-01-15", retrieved.getUpdateDate());
    }

    @Test
    @DisplayName("Should support Lombok equality")
    void testEquality() {
        MemberDto member = new MemberDto();
        member.setBioguideId("A000001");

        MemberResponse response1 = new MemberResponse();
        response1.setMembers(List.of(member));

        MemberResponse response2 = new MemberResponse();
        response2.setMembers(List.of(member));

        assertEquals(response1, response2);
    }

    @Test
    @DisplayName("Should support inequality when member lists differ")
    void testInequality() {
        MemberDto member1 = new MemberDto();
        member1.setBioguideId("A000001");

        MemberDto member2 = new MemberDto();
        member2.setBioguideId("B000001");

        MemberResponse response1 = new MemberResponse();
        response1.setMembers(List.of(member1));

        MemberResponse response2 = new MemberResponse();
        response2.setMembers(List.of(member2));

        assertNotEquals(response1, response2);
    }

    @Test
    @DisplayName("Should support Lombok hashCode")
    void testHashCode() {
        MemberDto member = new MemberDto();
        member.setBioguideId("A000001");

        MemberResponse response1 = new MemberResponse();
        response1.setMembers(List.of(member));

        MemberResponse response2 = new MemberResponse();
        response2.setMembers(List.of(member));

        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should provide meaningful toString()")
    void testToString() {
        MemberDto member = new MemberDto();
        member.setBioguideId("A000001");

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(member));

        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("MemberResponse"));
    }

    @Test
    @DisplayName("Should handle member list with special characters")
    void testMembersWithSpecialCharacters() {
        MemberDto member1 = new MemberDto();
        member1.setName("O'Brien-Smith Jr.");

        MemberDto member2 = new MemberDto();
        member2.setName("José García");

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(member1, member2));

        assertEquals("O'Brien-Smith Jr.", response.getMembers().get(0).getName());
        assertEquals("José García", response.getMembers().get(1).getName());
    }

    @Test
    @DisplayName("Should allow modification of member list")
    void testMemberListModification() {
        List<MemberDto> members = new ArrayList<>();
        MemberDto member1 = new MemberDto();
        member1.setBioguideId("A000001");
        members.add(member1);

        MemberResponse response = new MemberResponse();
        response.setMembers(members);

        assertEquals(1, response.getMembers().size());

        MemberDto member2 = new MemberDto();
        member2.setBioguideId("B000001");
        response.getMembers().add(member2);

        assertEquals(2, response.getMembers().size());
    }

    @Test
    @DisplayName("Should handle list replacement")
    void testListReplacement() {
        MemberDto member1 = new MemberDto();
        member1.setBioguideId("A000001");

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(member1));

        assertEquals(1, response.getMembers().size());

        MemberDto member2 = new MemberDto();
        member2.setBioguideId("B000001");

        response.setMembers(List.of(member2));

        assertEquals(1, response.getMembers().size());
        assertEquals("B000001", response.getMembers().get(0).getBioguideId());
    }

    @Test
    @DisplayName("Should handle mixed null and non-null values")
    void testMixedNullAndNonNullValues() {
        MemberDto member1 = new MemberDto();
        member1.setBioguideId("A000001");
        member1.setName("John Smith");

        MemberDto member2 = new MemberDto();
        // All fields null

        MemberDto member3 = new MemberDto();
        member3.setName("Jane Doe");

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(member1, member2, member3));

        assertEquals(3, response.getMembers().size());
        assertNotNull(response.getMembers().get(0).getName());
        assertNull(response.getMembers().get(1).getBioguideId());
        assertNotNull(response.getMembers().get(2).getName());
    }
}
