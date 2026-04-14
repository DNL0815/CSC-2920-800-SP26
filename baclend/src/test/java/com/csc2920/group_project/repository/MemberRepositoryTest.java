package com.csc2920.group_project.repository;

import com.csc2920.group_project.entity.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("MemberRepository Tests")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private MemberEntity currentMember1;
    private MemberEntity currentMember2;
    private MemberEntity historicalMember;

    @BeforeEach
    void setUp() {
        currentMember1 = new MemberEntity();
        currentMember1.setBioguideId("B001");
        currentMember1.setName("John Doe");
        currentMember1.setCurrentMember(true);
        currentMember1.setPartyName("D");
        currentMember1.setState("CA");

        currentMember2 = new MemberEntity();
        currentMember2.setBioguideId("B002");
        currentMember2.setName("Jane Smith");
        currentMember2.setCurrentMember(true);
        currentMember2.setPartyName("R");
        currentMember2.setState("NY");

        historicalMember = new MemberEntity();
        historicalMember.setBioguideId("B003");
        historicalMember.setName("Old Member");
        historicalMember.setCurrentMember(false);
        historicalMember.setPartyName("D");
        historicalMember.setState("TX");

        memberRepository.save(currentMember1);
        memberRepository.save(currentMember2);
        memberRepository.save(historicalMember);
    }

    // ============ findByBioguideId() Tests ============

    @Test
    @DisplayName("findByBioguideId returns member when found")
    void testFindByBioguideIdSuccess() {
        Optional<MemberEntity> result = memberRepository.findByBioguideId("B001");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("B001", result.get().getBioguideId());
    }

    @Test
    @DisplayName("findByBioguideId returns empty when not found")
    void testFindByBioguideIdNotFound() {
        Optional<MemberEntity> result = memberRepository.findByBioguideId("B999");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByBioguideId handles null bioguideId")
    void testFindByBioguideIdNull() {
        Optional<MemberEntity> result = memberRepository.findByBioguideId(null);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByBioguideId returns correct member from multiple results")
    void testFindByBioguideIdCorrectMember() {
        Optional<MemberEntity> result = memberRepository.findByBioguideId("B002");

        assertTrue(result.isPresent());
        assertEquals("Jane Smith", result.get().getName());
        assertEquals("NY", result.get().getState());
    }

    // ============ findByCurrentMemberTrue() Tests ============

    @Test
    @DisplayName("findByCurrentMemberTrue returns only current members")
    void testFindByCurrentMemberTrueSuccess() {
        List<MemberEntity> result = memberRepository.findByCurrentMemberTrue();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(MemberEntity::getCurrentMember));
    }

    @Test
    @DisplayName("findByCurrentMemberTrue excludes historical members")
    void testFindByCurrentMemberTrueExcludesHistorical() {
        List<MemberEntity> result = memberRepository.findByCurrentMemberTrue();

        assertFalse(result.stream().anyMatch(m -> "B003".equals(m.getBioguideId())));
    }

    @Test
    @DisplayName("findByCurrentMemberTrue returns empty when no current members")
    void testFindByCurrentMemberTrueEmpty() {
        memberRepository.deleteAll();

        List<MemberEntity> result = memberRepository.findByCurrentMemberTrue();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByCurrentMemberTrue contains all current member data")
    void testFindByCurrentMemberTrueContainsData() {
        List<MemberEntity> result = memberRepository.findByCurrentMemberTrue();

        assertTrue(result.stream().anyMatch(m -> "John Doe".equals(m.getName())));
        assertTrue(result.stream().anyMatch(m -> "Jane Smith".equals(m.getName())));
    }

    // ============ countByCurrentMemberTrue() Tests ============

    @Test
    @DisplayName("countByCurrentMemberTrue returns correct count")
    void testCountByCurrentMemberTrueSuccess() {
        long count = memberRepository.countByCurrentMemberTrue();

        assertEquals(2, count);
    }

    @Test
    @DisplayName("countByCurrentMemberTrue excludes historical members")
    void testCountByCurrentMemberTrueExcludesHistorical() {
        long count = memberRepository.countByCurrentMemberTrue();

        assertNotEquals(3, count);
    }

    @Test
    @DisplayName("countByCurrentMemberTrue returns zero when no current members")
    void testCountByCurrentMemberTrueZero() {
        memberRepository.delete(currentMember1);
        memberRepository.delete(currentMember2);

        long count = memberRepository.countByCurrentMemberTrue();

        assertEquals(0, count);
    }

    @Test
    @DisplayName("countByCurrentMemberTrue updates after new member saved")
    void testCountByCurrentMemberTrueAfterSave() {
        long countBefore = memberRepository.countByCurrentMemberTrue();

        MemberEntity newMember = new MemberEntity();
        newMember.setBioguideId("B004");
        newMember.setName("New Member");
        newMember.setCurrentMember(true);
        memberRepository.save(newMember);

        long countAfter = memberRepository.countByCurrentMemberTrue();

        assertEquals(countBefore + 1, countAfter);
    }

    // ============ CRUD Operations ============

    @Test
    @DisplayName("save persists member to database")
    void testSaveMember() {
        MemberEntity newMember = new MemberEntity();
        newMember.setBioguideId("B100");
        newMember.setName("Test Member");
        newMember.setCurrentMember(true);

        memberRepository.save(newMember);
        Optional<MemberEntity> retrieved = memberRepository.findByBioguideId("B100");

        assertTrue(retrieved.isPresent());
        assertEquals("Test Member", retrieved.get().getName());
    }

    @Test
    @DisplayName("save updates existing member")
    void testUpdateMember() {
        currentMember1.setName("Updated Name");
        memberRepository.save(currentMember1);

        Optional<MemberEntity> retrieved = memberRepository.findByBioguideId("B001");

        assertTrue(retrieved.isPresent());
        assertEquals("Updated Name", retrieved.get().getName());
    }

    @Test
    @DisplayName("delete removes member from database")
    void testDeleteMember() {
        memberRepository.delete(currentMember1);
        Optional<MemberEntity> retrieved = memberRepository.findByBioguideId("B001");

        assertFalse(retrieved.isPresent());
    }

    @Test
    @DisplayName("findById returns member when found")
    void testFindById() {
        Optional<MemberEntity> result = memberRepository.findById("B001");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    @DisplayName("findById returns empty when not found")
    void testFindByIdNotFound() {
        Optional<MemberEntity> result = memberRepository.findById("B999");

        assertFalse(result.isPresent());
    }
}
