package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LegislationSyncCoordinator {

    private final MemberRepository memberRepository;
    private final LegislationSyncWorker worker;

    public LegislationSyncCoordinator(MemberRepository memberRepository,
                                      LegislationSyncWorker worker) {
        this.memberRepository = memberRepository;
        this.worker = worker;
    }

    @Transactional(readOnly = true)
    public void syncAllLegislation() {

        List<MemberEntity> members = memberRepository.findByCurrentMemberTrue();

        System.out.println("Starting parallel legislation sync for " + members.size() + " current members.");

        for (MemberEntity member : members) {
            worker.syncMemberAsync(member.getBioguideId());
        }

        System.out.println("Parallel sync dispatched.");
    }

    @Transactional(readOnly = true)
    public void syncOne(MemberEntity member) {
        worker.syncMemberAsync(member.getBioguideId());
    }
}