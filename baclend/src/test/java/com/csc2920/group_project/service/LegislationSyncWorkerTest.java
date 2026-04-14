package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LegislationSyncWorker Tests")
class LegislationSyncWorkerTest {

    @Mock
    private LegislationSyncService syncService;

    @Mock
    private MemberRepository memberRepository;

    private LegislationSyncWorker legislationSyncWorker;

    @BeforeEach
    void setUp() {
        legislationSyncWorker = new LegislationSyncWorker(syncService, memberRepository);
    }

    // ============ syncMemberAsync() Tests ============

    @Test
    @DisplayName("syncMemberAsync calls syncService for current member")
    void testSyncMemberAsyncSuccess() {
        MemberEntity member = createMockMember("B001234", true);
        when(memberRepository.findById("B001234"))
            .thenReturn(Optional.of(member));

        legislationSyncWorker.syncMemberAsync("B001234");

        verify(memberRepository).findById("B001234");
        verify(syncService).syncLegislationForMember(member);
    }

    @Test
    @DisplayName("syncMemberAsync skips historical members")
    void testSyncMemberAsyncSkipsHistorical() {
        MemberEntity member = createMockMember("B001234", false);
        when(memberRepository.findById("B001234"))
            .thenReturn(Optional.of(member));

        legislationSyncWorker.syncMemberAsync("B001234");

        verify(memberRepository).findById("B001234");
        verify(syncService, never()).syncLegislationForMember(any());
    }

    @Test
    @DisplayName("syncMemberAsync handles member not found")
    void testSyncMemberAsyncMemberNotFound() {
        when(memberRepository.findById("B999999"))
            .thenReturn(Optional.empty());

        legislationSyncWorker.syncMemberAsync("B999999");

        verify(memberRepository).findById("B999999");
        verify(syncService, never()).syncLegislationForMember(any());
    }

    @Test
    @DisplayName("syncMemberAsync handles null bioguideId")
    void testSyncMemberAsyncNullBioguideId() {
        when(memberRepository.findById(null))
            .thenReturn(Optional.empty());

        legislationSyncWorker.syncMemberAsync(null);

        verify(memberRepository).findById(null);
        verify(syncService, never()).syncLegislationForMember(any());
    }

    @Test
    @DisplayName("syncMemberAsync handles service exception gracefully")
    void testSyncMemberAsyncServiceException() {
        MemberEntity member = createMockMember("B001234", true);
        when(memberRepository.findById("B001234"))
            .thenReturn(Optional.of(member));
        doThrow(new RuntimeException("Service error"))
            .when(syncService).syncLegislationForMember(member);

        legislationSyncWorker.syncMemberAsync("B001234");

        verify(memberRepository).findById("B001234");
        verify(syncService).syncLegislationForMember(member);
    }

    @Test
    @DisplayName("syncMemberAsync correctly retrieves managed entity from repository")
    void testSyncMemberAsyncRetrievesManaged() {
        MemberEntity member = createMockMember("B005678", true);
        when(memberRepository.findById("B005678"))
            .thenReturn(Optional.of(member));

        legislationSyncWorker.syncMemberAsync("B005678");

        verify(memberRepository).findById("B005678");
        verify(syncService).syncLegislationForMember(member);
    }

    @Test
    @DisplayName("syncMemberAsync handles member with currentMember = null")
    void testSyncMemberAsyncNullCurrentMemberFlag() {
        MemberEntity member = new MemberEntity();
        member.setBioguideId("B001234");
        member.setName("Test Member");
        member.setCurrentMember(null);
        
        when(memberRepository.findById("B001234"))
            .thenReturn(Optional.of(member));

        legislationSyncWorker.syncMemberAsync("B001234");

        verify(memberRepository).findById("B001234");
        verify(syncService).syncLegislationForMember(member);
    }

    // ============ Helper Methods ============

    private MemberEntity createMockMember(String bioguideId, boolean isCurrentMember) {
        MemberEntity member = new MemberEntity();
        member.setBioguideId(bioguideId);
        member.setName("Test Member");
        member.setCurrentMember(isCurrentMember);
        return member;
    }
}
