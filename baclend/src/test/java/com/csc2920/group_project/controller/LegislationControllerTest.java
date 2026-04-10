package com.csc2920.group_project.controller;

import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import com.csc2920.group_project.service.LegislationSyncCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Legislation Controller Tests")
class LegislationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LegislationSyncCoordinator coordinator;

    @Mock
    private MemberRepository memberRepository;

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
                new LegislationController(coordinator, memberRepository)
        ).build();
    }

    @Test
    @DisplayName("POST /api/legislation/sync/all - Should trigger sync for all current members")
    void testSyncAll() throws Exception {
        doNothing().when(coordinator).syncAllLegislation();

        mockMvc.perform(post("/api/legislation/sync/all"))
                .andExpect(status().isOk())
                .andExpect(content().string("Parallel legislation sync started for CURRENT members."));

        verify(coordinator, times(1)).syncAllLegislation();
    }

    @Test
    @DisplayName("POST /api/legislation/sync/{bioguideId} - Should sync legislation for valid current member")
    void testSyncOneWithValidCurrentMember() throws Exception {
        String bioguideId = "B001234";
        when(memberRepository.findByBioguideId(bioguideId)).thenReturn(Optional.of(testMember));
        doNothing().when(coordinator).syncOne(testMember);

        mockMvc.perform(post("/api/legislation/sync/{bioguideId}", bioguideId))
                .andExpect(status().isOk())
                .andExpect(content().string("Sync started for " + bioguideId));

        verify(memberRepository, times(1)).findByBioguideId(bioguideId);
        verify(coordinator, times(1)).syncOne(testMember);
    }

    @Test
    @DisplayName("POST /api/legislation/sync/{bioguideId} - Should handle member with null currentMember field")
    void testSyncOneWithNullCurrentMemberField() throws Exception {
        String bioguideId = "B001234";
        MemberEntity memberWithNullCurrentStatus = new MemberEntity();
        memberWithNullCurrentStatus.setBioguideId(bioguideId);
        memberWithNullCurrentStatus.setCurrentMember(null);

        when(memberRepository.findByBioguideId(bioguideId)).thenReturn(Optional.of(memberWithNullCurrentStatus));
        doNothing().when(coordinator).syncOne(memberWithNullCurrentStatus);

        mockMvc.perform(post("/api/legislation/sync/{bioguideId}", bioguideId))
                .andExpect(status().isOk())
                .andExpect(content().string("Sync started for " + bioguideId));

        verify(memberRepository, times(1)).findByBioguideId(bioguideId);
        verify(coordinator, times(1)).syncOne(memberWithNullCurrentStatus);
    }
}
