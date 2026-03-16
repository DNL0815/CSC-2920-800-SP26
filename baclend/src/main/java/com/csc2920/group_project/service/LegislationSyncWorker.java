package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LegislationSyncWorker {

    private final LegislationSyncService syncService;
    private final MemberRepository memberRepository;

    public LegislationSyncWorker(LegislationSyncService syncService,
                                 MemberRepository memberRepository) {
        this.syncService = syncService;
        this.memberRepository = memberRepository;
    }

    @Async("legislationExecutor")
    @Transactional
    public void syncMemberAsync(String bioguideId) {
        try {
            MemberEntity managed = memberRepository.findById(bioguideId).orElse(null);
            if (managed == null) {
                System.out.println("Member not found: " + bioguideId);
                return;
            }

            if (Boolean.FALSE.equals(managed.getCurrentMember())) {
                System.out.println("Skipping historical member: " + bioguideId);
                return;
            }

            System.out.println("Starting sync for member " + bioguideId);
            syncService.syncLegislationForMember(managed);
            System.out.println("Finished sync for member " + bioguideId);

        } catch (Exception e) {
            System.out.println("Error syncing member " + bioguideId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}