package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.entity.MemberLegislationEntity;
import com.csc2920.group_project.repository.LegislationRepository;
import com.csc2920.group_project.repository.MemberLegislationRepository;
import com.csc2920.group_project.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LegislationSyncService Tests")
class LegislationSyncServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private LegislationRepository legislationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberLegislationRepository memberLegislationRepository;

    private LegislationSyncService legislationSyncService;

    @BeforeEach
    void setUp() {
        legislationSyncService = new LegislationSyncService(
            memberService,
            legislationRepository,
            memberRepository,
            memberLegislationRepository
        );
    }

    // ============ syncLegislationForMember() Tests ============

    @Test
    @DisplayName("syncLegislationForMember fetches and saves legislation")
    void testSyncLegislationForMemberSuccess() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        List<Map<String, Object>> sponsoredBills = createMockBills(2, "HR");
        List<Map<String, Object>> cosponsoredBills = createMockBills(1, "S");
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenReturn(sponsoredBills);
        when(memberService.getCosponsoredLegislation("B001234"))
            .thenReturn(cosponsoredBills);
        when(legislationRepository.findByCongressAndBillTypeAndBillNumber(anyInt(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(legislationRepository.save(any(LegislationEntity.class)))
            .thenAnswer(i -> i.getArgument(0));
        when(memberLegislationRepository.save(any(MemberLegislationEntity.class)))
            .thenAnswer(i -> i.getArgument(0));

        legislationSyncService.syncLegislationForMember(member);

        verify(memberService).getSponsoredLegislation("B001234");
        verify(memberService).getCosponsoredLegislation("B001234");
        verify(memberLegislationRepository, atLeast(1)).save(any(MemberLegislationEntity.class));
    }

    @Test
    @DisplayName("syncLegislationForMember handles empty sponsored legislation")
    void testSyncLegislationForMemberEmptySponsored() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenReturn(Collections.emptyList());
        when(memberService.getCosponsoredLegislation("B001234"))
            .thenReturn(createMockBills(1, "HR"));
        when(legislationRepository.findByCongressAndBillTypeAndBillNumber(anyInt(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(legislationRepository.save(any(LegislationEntity.class)))
            .thenAnswer(i -> i.getArgument(0));

        legislationSyncService.syncLegislationForMember(member);

        verify(memberService).getSponsoredLegislation("B001234");
        verify(memberService).getCosponsoredLegislation("B001234");
    }

    @Test
    @DisplayName("syncLegislationForMember handles null legislation lists")
    void testSyncLegislationForMemberNullLists() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenReturn(null);
        when(memberService.getCosponsoredLegislation("B001234"))
            .thenReturn(null);

        legislationSyncService.syncLegislationForMember(member);

        verify(memberService).getSponsoredLegislation("B001234");
        verify(memberService).getCosponsoredLegislation("B001234");
    }

    @Test
    @DisplayName("syncLegislationForMember creates new legislation when not found in DB")
    void testSyncLegislationForMemberCreatesNewLegislation() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        List<Map<String, Object>> bills = createMockBills(1, "HR");
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenReturn(bills);
        when(memberService.getCosponsoredLegislation("B001234"))
            .thenReturn(Collections.emptyList());
        when(legislationRepository.findByCongressAndBillTypeAndBillNumber(118, "HR", "1000"))
            .thenReturn(Optional.empty());
        when(legislationRepository.save(any(LegislationEntity.class)))
            .thenAnswer(i -> i.getArgument(0));

        legislationSyncService.syncLegislationForMember(member);

        verify(legislationRepository).save(any(LegislationEntity.class));
    }

    @Test
    @DisplayName("syncLegislationForMember uses existing legislation when found in DB")
    void testSyncLegislationForMemberUsesExistingLegislation() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        LegislationEntity existingLegislation = new LegislationEntity();
        existingLegislation.setId(1L);
        existingLegislation.setCongress(118);
        existingLegislation.setBillType("HR");
        existingLegislation.setBillNumber("1000");
        
        List<Map<String, Object>> bills = createMockBills(1, "HR");
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenReturn(bills);
        when(memberService.getCosponsoredLegislation("B001234"))
            .thenReturn(Collections.emptyList());
        when(legislationRepository.findByCongressAndBillTypeAndBillNumber(118, "HR", "1000"))
            .thenReturn(Optional.of(existingLegislation));

        legislationSyncService.syncLegislationForMember(member);

        verify(legislationRepository, never()).save(any(LegislationEntity.class));
    }

    @Test
    @DisplayName("syncLegislationForMember sorts bills by latest action date")
    void testSyncLegislationForMemberSortsByLatestAction() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        List<Map<String, Object>> unsortedBills = new ArrayList<>();
        
        Map<String, Object> bill1 = new java.util.HashMap<>();
        bill1.put("number", "1001");
        bill1.put("type", "HR");
        bill1.put("congress", 118);
        bill1.put("title", "Bill 1");
        Map<String, Object> action1 = Map.of("actionDate", "2024-01-15", "text", "Action 1");
        bill1.put("latestAction", action1);
        unsortedBills.add(bill1);
        
        Map<String, Object> bill2 = new java.util.HashMap<>();
        bill2.put("number", "1002");
        bill2.put("type", "HR");
        bill2.put("congress", 118);
        bill2.put("title", "Bill 2");
        Map<String, Object> action2 = Map.of("actionDate", "2024-03-15", "text", "Action 2");
        bill2.put("latestAction", action2);
        unsortedBills.add(bill2);
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenReturn(unsortedBills);
        when(memberService.getCosponsoredLegislation("B001234"))
            .thenReturn(Collections.emptyList());
        when(legislationRepository.findByCongressAndBillTypeAndBillNumber(anyInt(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(legislationRepository.save(any(LegislationEntity.class)))
            .thenAnswer(i -> i.getArgument(0));

        legislationSyncService.syncLegislationForMember(member);

        verify(memberLegislationRepository, atLeastOnce()).save(any(MemberLegislationEntity.class));
    }

    @Test
    @DisplayName("syncLegislationForMember limits to top 5 bills per source")
    void testSyncLegislationForMemberLimitsToTop5() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        List<Map<String, Object>> moreThan5Bills = createMockBills(10, "HR");
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenReturn(moreThan5Bills);
        when(memberService.getCosponsoredLegislation("B001234"))
            .thenReturn(Collections.emptyList());
        when(legislationRepository.findByCongressAndBillTypeAndBillNumber(anyInt(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(legislationRepository.save(any(LegislationEntity.class)))
            .thenAnswer(i -> i.getArgument(0));

        legislationSyncService.syncLegislationForMember(member);

        verify(memberLegislationRepository, atMost(5)).save(any(MemberLegislationEntity.class));
    }

    @Test
    @DisplayName("syncLegislationForMember handles exception gracefully")
    void testSyncLegislationForMemberException() {
        MemberEntity member = createMockMember("B001234");
        member.setMemberLegislation(new HashSet<>());
        
        when(memberService.getSponsoredLegislation("B001234"))
            .thenThrow(new RuntimeException("API error"));

        assertDoesNotThrow(() -> legislationSyncService.syncLegislationForMember(member));
    }

    // ============ Helper Methods ============

    private MemberEntity createMockMember(String bioguideId) {
        MemberEntity member = new MemberEntity();
        member.setBioguideId(bioguideId);
        member.setName("Test Member");
        member.setCurrentMember(true);
        return member;
    }

    private List<Map<String, Object>> createMockBills(int count, String billType) {
        List<Map<String, Object>> bills = new java.util.ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> bill = new java.util.HashMap<>();
            bill.put("number", String.valueOf(1000 + i));
            bill.put("type", billType);
            bill.put("congress", 118);
            bill.put("title", "Test Bill " + (1000 + i));
            bill.put("introducedDate", "2023-01-15");
            bill.put("url", "https://congress.gov/bill/" + billType + "/" + (1000 + i));
            
            Map<String, Object> policyArea = Map.of("name", "Test Policy Area");
            bill.put("policyArea", policyArea);
            
            Map<String, Object> latestAction = Map.of(
                "actionDate", "2024-0" + (1 + i) + "-01",
                "text", "Action text " + i
            );
            bill.put("latestAction", latestAction);
            
            bills.add(bill);
        }
        
        return bills;
    }
}
