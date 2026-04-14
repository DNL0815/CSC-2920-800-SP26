package com.csc2920.group_project.controller;

import com.csc2920.group_project.dto.LegislationDto;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import com.csc2920.group_project.service.MemberLegislationService;
import com.csc2920.group_project.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Controller Tests")
class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberLegislationService memberLegislationService;

    private MemberEntity testMember;

    @BeforeEach
    void setUp() {
        testMember = new MemberEntity();
        testMember.setBioguideId("B001234");
        testMember.setName("John Doe");
        testMember.setCurrentMember(true);
        testMember.setPartyName("Democratic");
        testMember.setState("California");
        testMember.setChamber("House");

        mockMvc = MockMvcBuilders.standaloneSetup(
                new MemberController(memberService, memberRepository, memberLegislationService)
        ).build();
    }

    @Test
    @DisplayName("GET /api/members/db - Should return list of current members")
    void testGetMembersFromDb() throws Exception {
        List<MemberEntity> members = Arrays.asList(testMember);
        when(memberRepository.findByCurrentMemberTrue()).thenReturn(members);

        mockMvc.perform(get("/api/members/db"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bioguideId").value("B001234"))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].currentMember").value(true));

        verify(memberRepository, times(1)).findByCurrentMemberTrue();
    }

    @Test
    @DisplayName("GET /api/members/db - Should return empty list when no current members")
    void testGetMembersFromDbEmpty() throws Exception {
        when(memberRepository.findByCurrentMemberTrue()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/members/db"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(memberRepository, times(1)).findByCurrentMemberTrue();
    }

    @Test
    @DisplayName("POST /api/members/sync - Should trigger member sync")
    void testSyncMembers() throws Exception {
        doNothing().when(memberService).syncMembersToDatabase();

        mockMvc.perform(post("/api/members/sync"))
                .andExpect(status().isOk())
                .andExpect(content().string("Database synced with CURRENT members only."));

        verify(memberService, times(1)).syncMembersToDatabase();
    }

    @Test
    @DisplayName("GET /api/members/{bioguideId}/legislation - Should return all legislation for member")
    void testGetAllLegislation() throws Exception {
        String bioguideId = "B001234";
        LegislationDto legislation1 = new LegislationDto(118, "hr", "1234", "2023-01-15", "Test Bill 1", 
                null, "2023-02-20", null, null, "Congress");
        LegislationDto legislation2 = new LegislationDto(118, "hr", "5678", "2023-02-01", "Test Bill 2", 
                null, "2023-03-15", null, null, "Congress");

        List<LegislationDto> legislationList = Arrays.asList(legislation1, legislation2);
        when(memberLegislationService.getAllForMember(bioguideId)).thenReturn(legislationList);

        mockMvc.perform(get("/api/members/{bioguideId}/legislation", bioguideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Test Bill 1"))
                .andExpect(jsonPath("$[1].title").value("Test Bill 2"));

        verify(memberLegislationService, times(1)).getAllForMember(bioguideId);
    }

    @Test
    @DisplayName("GET /api/members/{bioguideId}/legislation - Should return empty list when no legislation")
    void testGetAllLegislationEmpty() throws Exception {
        String bioguideId = "B001234";
        when(memberLegislationService.getAllForMember(bioguideId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/members/{bioguideId}/legislation", bioguideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(memberLegislationService, times(1)).getAllForMember(bioguideId);
    }

    @Test
    @DisplayName("GET /api/members/{bioguideId}/legislation/sponsored - Should return sponsored bills")
    void testGetSponsoredLegislation() throws Exception {
        String bioguideId = "B001234";
        LegislationDto sponsored = new LegislationDto(118, "hr", "1234", "2023-01-15", "Sponsored Bill", 
                null, "2023-02-20", null, null, "Congress");

        when(memberLegislationService.getSponsoredForMember(bioguideId))
                .thenReturn(Arrays.asList(sponsored));

        mockMvc.perform(get("/api/members/{bioguideId}/legislation/sponsored", bioguideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Sponsored Bill"));

        verify(memberLegislationService, times(1)).getSponsoredForMember(bioguideId);
    }

    @Test
    @DisplayName("GET /api/members/{bioguideId}/legislation/sponsored - Should return empty list when no sponsored bills")
    void testGetSponsoredLegislationEmpty() throws Exception {
        String bioguideId = "B001234";
        when(memberLegislationService.getSponsoredForMember(bioguideId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/members/{bioguideId}/legislation/sponsored", bioguideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(memberLegislationService, times(1)).getSponsoredForMember(bioguideId);
    }

    @Test
    @DisplayName("GET /api/members/{bioguideId}/legislation/cosponsored - Should return cosponsored bills")
    void testGetCosponsoredLegislation() throws Exception {
        String bioguideId = "B001234";
        LegislationDto cosponsored = new LegislationDto(118, "hr", "9999", "2023-02-01", "Cosponsored Bill", 
                null, "2023-03-15", null, null, "Congress");

        when(memberLegislationService.getCosponsoredForMember(bioguideId))
                .thenReturn(Arrays.asList(cosponsored));

        mockMvc.perform(get("/api/members/{bioguideId}/legislation/cosponsored", bioguideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Cosponsored Bill"));

        verify(memberLegislationService, times(1)).getCosponsoredForMember(bioguideId);
    }

    @Test
    @DisplayName("GET /api/members/{bioguideId}/legislation/cosponsored - Should return empty list when no cosponsored bills")
    void testGetCosponsoredLegislationEmpty() throws Exception {
        String bioguideId = "B001234";
        when(memberLegislationService.getCosponsoredForMember(bioguideId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/members/{bioguideId}/legislation/cosponsored", bioguideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(memberLegislationService, times(1)).getCosponsoredForMember(bioguideId);
    }
}
