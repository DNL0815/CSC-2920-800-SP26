package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LegislationSyncCoordinator Tests")
class LegislationSyncCoordinatorTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LegislationSyncWorker worker;

    private LegislationSyncCoordinator legislationSyncCoordinator;

    @BeforeEach
    void setUp() {
        legislationSyncCoordinator = new LegislationSyncCoordinator(memberRepository, worker);
    }

    // ============ syncAllLegislation() Tests ============

    @Test
    @DisplayName("syncAllLegislation dispatches sync for all current members")
    void testSyncAllLegislationSuccess() {
        List<MemberEntity> members = createMockMembers(5);
        when(memberRepository.findByCurrentMemberTrue()).thenReturn(members);

        legislationSyncCoordinator.syncAllLegislation();

        verify(memberRepository).findByCurrentMemberTrue();
        verify(worker, times(5)).syncMemberAsync(anyString());
    }

    @Test
    @DisplayName("syncAllLegislation handles empty member list")
    void testSyncAllLegislationEmptyMembers() {
        when(memberRepository.findByCurrentMemberTrue()).thenReturn(Collections.emptyList());

        legislationSyncCoordinator.syncAllLegislation();

        verify(memberRepository).findByCurrentMemberTrue();
        verify(worker, never()).syncMemberAsync(anyString());
    }

    @Test
    @DisplayName("syncAllLegislation handles single member")
    void testSyncAllLegislationSingleMember() {
        List<MemberEntity> members = createMockMembers(1);
        when(memberRepository.findByCurrentMemberTrue()).thenReturn(members);

        legislationSyncCoordinator.syncAllLegislation();

        verify(memberRepository).findByCurrentMemberTrue();
        verify(worker, times(1)).syncMemberAsync("B000001");
    }

    @Test
    @DisplayName("syncAllLegislation handles large member list")
    void testSyncAllLegislationLargeList() {
        List<MemberEntity> members = createMockMembers(538);
        when(memberRepository.findByCurrentMemberTrue()).thenReturn(members);

        legislationSyncCoordinator.syncAllLegislation();

        verify(memberRepository).findByCurrentMemberTrue();
        verify(worker, times(538)).syncMemberAsync(anyString());
    }

    @Test
    @DisplayName("syncAllLegislation correctly passes bioguide IDs to worker")
    void testSyncAllLegislationPassesBioguideIds() {
        List<MemberEntity> members = new ArrayList<>();
        MemberEntity member1 = new MemberEntity();
        member1.setBioguideId("B001234");
        members.add(member1);
        
        MemberEntity member2 = new MemberEntity();
        member2.setBioguideId("B005678");
        members.add(member2);

        when(memberRepository.findByCurrentMemberTrue()).thenReturn(members);

        legislationSyncCoordinator.syncAllLegislation();

        verify(worker).syncMemberAsync("B001234");
        verify(worker).syncMemberAsync("B005678");
    }

    // ============ syncOne() Tests ============

    @Test
    @DisplayName("syncOne dispatches sync for single member")
    void testSyncOneSuccess() {
        MemberEntity member = createMockMember("B001234");

        legislationSyncCoordinator.syncOne(member);

        verify(worker).syncMemberAsync("B001234");
    }

    @Test
    @DisplayName("syncOne passes correct bioguide ID to worker")
    void testSyncOnePassesBioguideId() {
        MemberEntity member = new MemberEntity();
        member.setBioguideId("B009999");

        legislationSyncCoordinator.syncOne(member);

        verify(worker).syncMemberAsync("B009999");
    }

    @Test
    @DisplayName("syncOne handles member with null bioguideId")
    void testSyncOneNullBioguideId() {
        MemberEntity member = new MemberEntity();
        member.setBioguideId(null);

        legislationSyncCoordinator.syncOne(member);

        verify(worker).syncMemberAsync(null);
    }

    @Test
    @DisplayName("syncOne calls worker exactly once")
    void testSyncOneCallsWorkerOnce() {
        MemberEntity member = createMockMember("B001234");

        legislationSyncCoordinator.syncOne(member);

        verify(worker, times(1)).syncMemberAsync("B001234");
    }

    // ============ Helper Methods ============

    private List<MemberEntity> createMockMembers(int count) {
        List<MemberEntity> members = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            MemberEntity member = new MemberEntity();
            member.setBioguideId("B" + String.format("%06d", i));
            member.setName("Member " + i);
            member.setCurrentMember(true);
            members.add(member);
        }
        return members;
    }

    private MemberEntity createMockMember(String bioguideId) {
        MemberEntity member = new MemberEntity();
        member.setBioguideId(bioguideId);
        member.setName("Test Member");
        member.setCurrentMember(true);
        return member;
    }
}
