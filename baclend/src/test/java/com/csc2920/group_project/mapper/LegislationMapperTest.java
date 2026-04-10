package com.csc2920.group_project.mapper;

import com.csc2920.group_project.dto.LegislationDto;
import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.entity.MemberLegislationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LegislationMapper Tests")
class LegislationMapperTest {

    private MemberEntity member;
    private LegislationEntity legislation;
    private MemberLegislationEntity memberLegislation;

    @BeforeEach
    void setUp() {
        member = new MemberEntity();
        member.setBioguideId("B001");
        member.setName("John Doe");

        legislation = new LegislationEntity();
        legislation.setId(1L);
        legislation.setCongress(118);
        legislation.setBillType("hr");
        legislation.setBillNumber("1234");
        legislation.setIntroducedDate("2023-01-01");
        legislation.setTitle("An Act to Test");
        legislation.setPolicyArea("Government Operations");
        legislation.setLatestActionDate("2023-06-15");
        legislation.setLatestActionText("Referred to Committee");
        legislation.setUrl("https://congress.gov/bill/118/hr/1234");

        memberLegislation = new MemberLegislationEntity();
        memberLegislation.setId(1L);
        memberLegislation.setMember(member);
        memberLegislation.setLegislation(legislation);
        memberLegislation.setSource("sponsor");
    }

    @Test
    @DisplayName("Should map MemberLegislationEntity to LegislationDto")
    void testToDtoMapsAllFields() {
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertNotNull(dto);
        assertEquals(118, dto.congress());
        assertEquals("hr", dto.billType());
        assertEquals("1234", dto.billNumber());
        assertEquals("2023-01-01", dto.introducedDate());
        assertEquals("An Act to Test", dto.title());
        assertEquals("Government Operations", dto.policyArea());
        assertEquals("2023-06-15", dto.latestActionDate());
        assertEquals("Referred to Committee", dto.latestActionText());
        assertEquals("https://congress.gov/bill/118/hr/1234", dto.url());
        assertEquals("sponsor", dto.source());
    }

    @Test
    @DisplayName("Should return null when MemberLegislationEntity is null")
    void testToDtoWithNullInput() {
        LegislationDto dto = LegislationMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("Should map null legislation properties to null in dto")
    void testToDtoWithNullLegislationProperties() {
        LegislationEntity leg = new LegislationEntity();
        leg.setId(1L);
        leg.setCongress(118);
        leg.setBillType("hr");
        leg.setBillNumber("1234");
        // All other properties left null

        memberLegislation.setLegislation(leg);
        memberLegislation.setSource("cosponsor");

        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertNotNull(dto);
        assertEquals(118, dto.congress());
        assertEquals("hr", dto.billType());
        assertEquals("1234", dto.billNumber());
        assertNull(dto.introducedDate());
        assertNull(dto.title());
        assertNull(dto.policyArea());
        assertNull(dto.latestActionDate());
        assertNull(dto.latestActionText());
        assertNull(dto.url());
        assertEquals("cosponsor", dto.source());
    }

    @Test
    @DisplayName("Should map sponsor source correctly")
    void testToDtoWithSponsorSource() {
        memberLegislation.setSource("sponsor");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("sponsor", dto.source());
    }

    @Test
    @DisplayName("Should map cosponsor source correctly")
    void testToDtoWithCosponsorSource() {
        memberLegislation.setSource("cosponsor");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("cosponsor", dto.source());
    }

    @Test
    @DisplayName("Should map custom source correctly")
    void testToDtoWithCustomSource() {
        memberLegislation.setSource("actor");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("actor", dto.source());
    }

    @Test
    @DisplayName("Should handle null source")
    void testToDtoWithNullSource() {
        memberLegislation.setSource(null);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertNull(dto.source());
    }

    @Test
    @DisplayName("Should preserve congress value")
    void testToDtoPreservesCongress() {
        legislation.setCongress(119);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals(119, dto.congress());
    }

    @Test
    @DisplayName("Should handle null congress")
    void testToDtoWithNullCongress() {
        legislation.setCongress(null);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertNull(dto.congress());
    }

    @Test
    @DisplayName("Should preserve bill type case")
    void testToDtoPreservesBillTypeCase() {
        legislation.setBillType("HJRES");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("HJRES", dto.billType());
    }

    @Test
    @DisplayName("Should handle empty string bill number")
    void testToDtoWithEmptyBillNumber() {
        legislation.setBillNumber("");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("", dto.billNumber());
    }

    @Test
    @DisplayName("Should handle long bill title")
    void testToDtoWithLongTitle() {
        String longTitle = "An Act to establish and provide funding for the maintenance and restoration of " +
                          "certain public lands and facilities for the benefit of the American people and veterans.";
        legislation.setTitle(longTitle);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals(longTitle, dto.title());
    }

    @Test
    @DisplayName("Should handle URL preservation")
    void testToDtoPreservesUrl() {
        String url = "https://congress.gov/bill/118/hr/1234";
        legislation.setUrl(url);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals(url, dto.url());
    }

    @Test
    @DisplayName("Should preserve all fields in complete mapping")
    void testToDtoCompleteMapping() {
        memberLegislation.setSource("sponsor");

        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertAll("All fields should be preserved",
            () -> assertEquals(legislation.getCongress(), dto.congress()),
            () -> assertEquals(legislation.getBillType(), dto.billType()),
            () -> assertEquals(legislation.getBillNumber(), dto.billNumber()),
            () -> assertEquals(legislation.getIntroducedDate(), dto.introducedDate()),
            () -> assertEquals(legislation.getTitle(), dto.title()),
            () -> assertEquals(legislation.getPolicyArea(), dto.policyArea()),
            () -> assertEquals(legislation.getLatestActionDate(), dto.latestActionDate()),
            () -> assertEquals(legislation.getLatestActionText(), dto.latestActionText()),
            () -> assertEquals(legislation.getUrl(), dto.url()),
            () -> assertEquals(memberLegislation.getSource(), dto.source())
        );
    }

    @Test
    @DisplayName("Should handle null policy area")
    void testToDtoWithNullPolicyArea() {
        legislation.setPolicyArea(null);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertNull(dto.policyArea());
    }

    @Test
    @DisplayName("Should handle null latest action fields")
    void testToDtoWithNullLatestAction() {
        legislation.setLatestActionDate(null);
        legislation.setLatestActionText(null);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertNull(dto.latestActionDate());
        assertNull(dto.latestActionText());
    }

    @Test
    @DisplayName("Should be idempotent - multiple calls return equivalent objects")
    void testToDtoIdempotency() {
        LegislationDto dto1 = LegislationMapper.toDto(memberLegislation);
        LegislationDto dto2 = LegislationMapper.toDto(memberLegislation);

        assertEquals(dto1.congress(), dto2.congress());
        assertEquals(dto1.billType(), dto2.billType());
        assertEquals(dto1.billNumber(), dto2.billNumber());
        assertEquals(dto1.source(), dto2.source());
    }

    @Test
    @DisplayName("Should extract source from link entity, not legislation entity")
    void testToDtoSourceComesFromLinkEntity() {
        // source is a property of MemberLegislationEntity, not LegislationEntity
        memberLegislation.setSource("cosponsor");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("cosponsor", dto.source());
        // Verify it's coming from the link, not legislation
        assertNotEquals("cosponsor", legislation.getTitle());
    }

    @Test
    @DisplayName("Should handle special characters in bill type")
    void testToDtoWithSpecialCharactersInBillType() {
        legislation.setBillType("HR-SPEC");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("HR-SPEC", dto.billType());
    }

    @Test
    @DisplayName("Should handle special characters in title")
    void testToDtoWithSpecialCharactersInTitle() {
        legislation.setTitle("An Act to establish 'Healthcare Reform' & improve (services) for 2024");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("An Act to establish 'Healthcare Reform' & improve (services) for 2024", dto.title());
    }

    @Test
    @DisplayName("Should handle unicode in policy area")
    void testToDtoWithUnicodeInPolicyArea() {
        legislation.setPolicyArea("Health 中文 日本語");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("Health 中文 日本語", dto.policyArea());
    }

    @Test
    @DisplayName("Should handle unicode in action text")
    void testToDtoWithUnicodeInActionText() {
        legislation.setLatestActionText("Passed Chamber with modifications: 改正 修正");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("Passed Chamber with modifications: 改正 修正", dto.latestActionText());
    }

    @Test
    @DisplayName("Should preserve very long strings")
    void testToDtoWithVeryLongStrings() {
        String longTitle = "A".repeat(1000);
        String longAction = "B".repeat(1000);
        legislation.setTitle(longTitle);
        legislation.setLatestActionText(longAction);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals(longTitle, dto.title());
        assertEquals(longAction, dto.latestActionText());
    }

    @Test
    @DisplayName("Should handle edge case congress numbers")
    void testToDtoWithEdgeCaseCongressNumbers() {
        legislation.setCongress(1);
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);
        assertEquals(1, dto.congress());

        legislation.setCongress(999);
        dto = LegislationMapper.toDto(memberLegislation);
        assertEquals(999, dto.congress());
    }

    @Test
    @DisplayName("Should preserve source with special characters")
    void testToDtoWithSpecialCharactersInSource() {
        memberLegislation.setSource("co-sponsor_primary");
        LegislationDto dto = LegislationMapper.toDto(memberLegislation);

        assertEquals("co-sponsor_primary", dto.source());
    }

    @Test
    @DisplayName("Should handle multiple successive mappings preserve consistency")
    void testToDtoMultipleCallsConsistency() {
        LegislationDto dto1 = LegislationMapper.toDto(memberLegislation);
        LegislationDto dto2 = LegislationMapper.toDto(memberLegislation);
        LegislationDto dto3 = LegislationMapper.toDto(memberLegislation);

        assertEquals(dto1.congress(), dto2.congress());
        assertEquals(dto2.congress(), dto3.congress());
        assertEquals(dto1.billType(), dto2.billType());
        assertEquals(dto2.billType(), dto3.billType());
    }
}
