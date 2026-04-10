package com.csc2920.group_project.repository;

import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.entity.MemberLegislationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("MemberLegislationRepository Tests")
class MemberLegislationRepositoryTest {

    @Autowired
    private MemberLegislationRepository memberLegislationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LegislationRepository legislationRepository;

    private MemberEntity member1;
    private MemberEntity member2;
    private LegislationEntity bill1;
    private LegislationEntity bill2;
    private LegislationEntity bill3;
    private MemberLegislationEntity sponsoredLink1;
    private MemberLegislationEntity sponsoredLink2;
    private MemberLegislationEntity cosponsoredLink1;

    @BeforeEach
    void setUp() {
        member1 = new MemberEntity();
        member1.setBioguideId("B001");
        member1.setName("John Doe");
        member1.setCurrentMember(true);
        memberRepository.save(member1);

        member2 = new MemberEntity();
        member2.setBioguideId("B002");
        member2.setName("Jane Smith");
        member2.setCurrentMember(true);
        memberRepository.save(member2);

        bill1 = new LegislationEntity();
        bill1.setCongress(118);
        bill1.setBillType("HR");
        bill1.setBillNumber("1234");
        bill1.setTitle("Bill 1");
        legislationRepository.save(bill1);

        bill2 = new LegislationEntity();
        bill2.setCongress(118);
        bill2.setBillType("HR");
        bill2.setBillNumber("5678");
        bill2.setTitle("Bill 2");
        legislationRepository.save(bill2);

        bill3 = new LegislationEntity();
        bill3.setCongress(118);
        bill3.setBillType("S");
        bill3.setBillNumber("9999");
        bill3.setTitle("Bill 3");
        legislationRepository.save(bill3);

        sponsoredLink1 = new MemberLegislationEntity();
        sponsoredLink1.setMember(member1);
        sponsoredLink1.setLegislation(bill1);
        sponsoredLink1.setSource("sponsored");
        memberLegislationRepository.save(sponsoredLink1);

        sponsoredLink2 = new MemberLegislationEntity();
        sponsoredLink2.setMember(member1);
        sponsoredLink2.setLegislation(bill2);
        sponsoredLink2.setSource("sponsored");
        memberLegislationRepository.save(sponsoredLink2);

        cosponsoredLink1 = new MemberLegislationEntity();
        cosponsoredLink1.setMember(member1);
        cosponsoredLink1.setLegislation(bill3);
        cosponsoredLink1.setSource("cosponsored");
        memberLegislationRepository.save(cosponsoredLink1);
    }

    // ============ findByMember_BioguideId() Tests ============

    @Test
    @DisplayName("findByMember_BioguideId returns all legislation for member")
    void testFindByMemberBioguideIdSuccess() {
        List<MemberLegislationEntity> result = memberLegislationRepository.findByMember_BioguideId("B001");

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findByMember_BioguideId includes both sponsored and cosponsored")
    void testFindByMemberBioguideIdIncludesBothSources() {
        List<MemberLegislationEntity> result = memberLegislationRepository.findByMember_BioguideId("B001");

        assertTrue(result.stream().anyMatch(m -> "sponsored".equals(m.getSource())));
        assertTrue(result.stream().anyMatch(m -> "cosponsored".equals(m.getSource())));
    }

    @Test
    @DisplayName("findByMember_BioguideId returns empty when no legislation")
    void testFindByMemberBioguideIdEmpty() {
        List<MemberLegislationEntity> result = memberLegislationRepository.findByMember_BioguideId("B002");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByMember_BioguideId returns empty when member not found")
    void testFindByMemberBioguideIdNotFound() {
        List<MemberLegislationEntity> result = memberLegislationRepository.findByMember_BioguideId("B999");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByMember_BioguideId returns correct member's legislation")
    void testFindByMemberBioguideIdCorrectMember() {
        List<MemberLegislationEntity> result = memberLegislationRepository.findByMember_BioguideId("B001");

        assertTrue(result.stream().allMatch(m -> "B001".equals(m.getMember().getBioguideId())));
    }

    // ============ findByMember_BioguideIdAndSource() Tests ============

    @Test
    @DisplayName("findByMember_BioguideIdAndSource returns only sponsored legislation")
    void testFindByMemberBioguideIdAndSourceSponsored() {
        List<MemberLegislationEntity> result = memberLegislationRepository
            .findByMember_BioguideIdAndSource("B001", "sponsored");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> "sponsored".equals(m.getSource())));
    }

    @Test
    @DisplayName("findByMember_BioguideIdAndSource returns only cosponsored legislation")
    void testFindByMemberBioguideIdAndSourceCosponsored() {
        List<MemberLegislationEntity> result = memberLegislationRepository
            .findByMember_BioguideIdAndSource("B001", "cosponsored");

        assertEquals(1, result.size());
        assertEquals("cosponsored", result.get(0).getSource());
    }

    @Test
    @DisplayName("findByMember_BioguideIdAndSource returns empty when no matching source")
    void testFindByMemberBioguideIdAndSourceNoMatch() {
        List<MemberLegislationEntity> result = memberLegislationRepository
            .findByMember_BioguideIdAndSource("B001", "unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByMember_BioguideIdAndSource returns empty when member not found")
    void testFindByMemberBioguideIdAndSourceMemberNotFound() {
        List<MemberLegislationEntity> result = memberLegislationRepository
            .findByMember_BioguideIdAndSource("B999", "sponsored");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByMember_BioguideIdAndSource is case-sensitive for source")
    void testFindByMemberBioguideIdAndSourceCaseSensitive() {
        List<MemberLegislationEntity> result = memberLegislationRepository
            .findByMember_BioguideIdAndSource("B001", "Sponsored");

        assertTrue(result.isEmpty());
    }

    // ============ CRUD Operations ============

    @Test
    @DisplayName("save persists member-legislation link to database")
    void testSaveMemberLegislation() {
        MemberLegislationEntity newLink = new MemberLegislationEntity();
        newLink.setMember(member2);
        newLink.setLegislation(bill1);
        newLink.setSource("sponsored");

        memberLegislationRepository.save(newLink);
        List<MemberLegislationEntity> result = memberLegislationRepository
            .findByMember_BioguideIdAndSource("B002", "sponsored");

        assertEquals(1, result.size());
        assertEquals("B002", result.get(0).getMember().getBioguideId());
    }

    @Test
    @DisplayName("save updates existing link")
    void testUpdateMemberLegislation() {
        sponsoredLink1.setSource("cosponsored");
        memberLegislationRepository.save(sponsoredLink1);

        MemberLegislationEntity retrieved = memberLegislationRepository.findById(sponsoredLink1.getId()).get();

        assertEquals("cosponsored", retrieved.getSource());
    }

    @Test
    @DisplayName("delete removes link from database")
    void testDeleteMemberLegislation() {
        memberLegislationRepository.delete(sponsoredLink1);
        List<MemberLegislationEntity> result = memberLegislationRepository.findByMember_BioguideId("B001");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findById returns link when found")
    void testFindById() {
        var result = memberLegislationRepository.findById(sponsoredLink1.getId());

        assertTrue(result.isPresent());
        assertEquals("B001", result.get().getMember().getBioguideId());
        assertEquals("1234", result.get().getLegislation().getBillNumber());
    }

    @Test
    @DisplayName("findAll returns all links")
    void testFindAll() {
        var result = memberLegislationRepository.findAll();

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("deleteAll removes all links")
    void testDeleteAll() {
        memberLegislationRepository.deleteAll();
        var result = memberLegislationRepository.findAll();

        assertTrue(result.isEmpty());
    }
}
