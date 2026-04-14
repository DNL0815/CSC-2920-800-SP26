package com.csc2920.group_project.service;

import com.csc2920.group_project.dto.MemberDto;
import com.csc2920.group_project.dto.MemberResponse;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService Tests")
class MemberServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(restTemplate, memberRepository, 
            "https://api.congress.gov/v3", "test-key");
    }

    // ============ getAllMembers() Tests ============

    @Test
    @DisplayName("getAllMembers returns list of members from API")
    void testGetAllMembersSuccess() {
        MemberResponse response = createMockMemberResponse(5);
        when(restTemplate.getForObject(anyString(), eq(MemberResponse.class)))
            .thenReturn(response);

        List<MemberDto> result = memberService.getAllMembers();

        assertNotNull(result);
        assertTrue(result.size() <= 538);
        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(MemberResponse.class));
    }

    @Test
    @DisplayName("getAllMembers handles null response from API")
    void testGetAllMembersNullResponse() {
        when(restTemplate.getForObject(anyString(), eq(MemberResponse.class)))
            .thenReturn(null);

        List<MemberDto> result = memberService.getAllMembers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllMembers handles empty members list")
    void testGetAllMembersEmptyList() {
        MemberResponse response = new MemberResponse();
        response.setMembers(Collections.emptyList());
        when(restTemplate.getForObject(anyString(), eq(MemberResponse.class)))
            .thenReturn(response);

        List<MemberDto> result = memberService.getAllMembers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllMembers paginates through results")
    void testGetAllMembersPagination() {
        MemberResponse page1 = createMockMemberResponse(250);
        MemberResponse page2 = createMockMemberResponse(250);
        MemberResponse page3 = createMockMemberResponse(38);

        when(restTemplate.getForObject(anyString(), eq(MemberResponse.class)))
            .thenReturn(page1)
            .thenReturn(page2)
            .thenReturn(page3);

        List<MemberDto> result = memberService.getAllMembers();

        assertEquals(538, result.size());
        verify(restTemplate, atLeast(3)).getForObject(anyString(), eq(MemberResponse.class));
    }

    // ============ syncMembersToDatabase() Tests ============

    @Test
    @DisplayName("syncMembersToDatabase skips sync when members already exist")
    void testSyncMembersDatabaseSkips() {
        when(memberRepository.countByCurrentMemberTrue()).thenReturn(538L);

        memberService.syncMembersToDatabase();

        verify(memberRepository, times(1)).countByCurrentMemberTrue();
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    @DisplayName("syncMembersToDatabase saves members to repository")
    void testSyncMembersDatabaseSaves() {
        MemberResponse response = createMockMemberResponse(3);
        
        when(memberRepository.countByCurrentMemberTrue()).thenReturn(0L);
        when(restTemplate.getForObject(anyString(), eq(MemberResponse.class)))
            .thenReturn(response)
            .thenReturn(new MemberResponse());
        when(memberRepository.save(any(MemberEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(memberRepository.countByCurrentMemberTrue()).thenReturn(3L);

        memberService.syncMembersToDatabase();

        verify(memberRepository, atLeast(3)).save(any(MemberEntity.class));
        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(MemberResponse.class));
    }

    @Test
    @DisplayName("syncMembersToDatabase skips malformed members")
    void testSyncMembersDatabaseSkipsMalformed() {
        MemberDto validMember = new MemberDto();
        validMember.setBioguideId("B001234");
        validMember.setName("John Doe");
        validMember.setPartyName("D");
        validMember.setState("CA");

        MemberDto malformedMember = new MemberDto();
        malformedMember.setBioguideId(null);

        MemberResponse response = new MemberResponse();
        response.setMembers(List.of(validMember, malformedMember));

        when(memberRepository.countByCurrentMemberTrue()).thenReturn(0L);
        when(restTemplate.getForObject(anyString(), eq(MemberResponse.class)))
            .thenReturn(response)
            .thenReturn(new MemberResponse());
        when(memberRepository.save(any(MemberEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(memberRepository.countByCurrentMemberTrue()).thenReturn(1L);

        memberService.syncMembersToDatabase();

        verify(memberRepository, times(1)).save(any(MemberEntity.class));
    }

    // ============ getSponsoredLegislation() Tests ============

    @Test
    @DisplayName("getSponsoredLegislation returns list of bills")
    void testGetSponsoredLegislationSuccess() {
        Map<String, Object> response = Map.of(
            "sponsoredLegislation", List.of(
                Map.of("type", "HR", "number", "1234", "title", "Test Bill 1"),
                Map.of("type", "HR", "number", "5678", "title", "Test Bill 2")
            )
        );
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenReturn(response);

        List<Map<String, Object>> result = memberService.getSponsoredLegislation("B001234");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    @DisplayName("getSponsoredLegislation handles null response")
    void testGetSponsoredLegislationNullResponse() {
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenReturn(null);

        List<Map<String, Object>> result = memberService.getSponsoredLegislation("B001234");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getSponsoredLegislation handles missing sponsoredLegislation key")
    void testGetSponsoredLegislationMissingKey() {
        Map<String, Object> response = Map.of("otherKey", "value");
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenReturn(response);

        List<Map<String, Object>> result = memberService.getSponsoredLegislation("B001234");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ============ getCosponsoredLegislation() Tests ============

    @Test
    @DisplayName("getCosponsoredLegislation returns list of bills")
    void testGetCosponsoredLegislationSuccess() {
        Map<String, Object> response = Map.of(
            "cosponsoredLegislation", List.of(
                Map.of("type", "HR", "number", "1111", "title", "Cosponsored Bill 1")
            )
        );
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenReturn(response);

        List<Map<String, Object>> result = memberService.getCosponsoredLegislation("B001234");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    @DisplayName("getCosponsoredLegislation handles empty list")
    void testGetCosponsoredLegislationEmpty() {
        Map<String, Object> response = Map.of("cosponsoredLegislation", Collections.emptyList());
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenReturn(response);

        List<Map<String, Object>> result = memberService.getCosponsoredLegislation("B001234");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ============ Helper Methods ============

    private MemberResponse createMockMemberResponse(int count) {
        MemberResponse response = new MemberResponse();
        List<MemberDto> members = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            MemberDto dto = new MemberDto();
            dto.setBioguideId("B00" + i);
            dto.setName("Member " + i);
            dto.setPartyName("D");
            dto.setState("CA");
            members.add(dto);
        }

        response.setMembers(members);
        return response;
    }
}
