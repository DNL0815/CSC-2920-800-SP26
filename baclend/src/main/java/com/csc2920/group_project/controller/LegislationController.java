package com.csc2920.group_project.controller;

import com.csc2920.group_project.service.LegislationSyncCoordinator;
import com.csc2920.group_project.repository.MemberRepository;
import com.csc2920.group_project.entity.MemberEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/legislation")
public class LegislationController {

    private final LegislationSyncCoordinator coordinator;
    private final MemberRepository memberRepository;

    public LegislationController(LegislationSyncCoordinator coordinator,
                                 MemberRepository memberRepository) {
        this.coordinator = coordinator;
        this.memberRepository = memberRepository;
    }

    // ---------------------------------------------------------
    // Sync legislation for ALL CURRENT members
    // ---------------------------------------------------------
    @PostMapping("/sync/all")
    public String syncAll() {
        coordinator.syncAllLegislation();
        return "Parallel legislation sync started for CURRENT members.";
    }

    // ---------------------------------------------------------
    // Sync legislation for ONE CURRENT member
    // ---------------------------------------------------------
    @PostMapping("/sync/{bioguideId}")
    public String syncOne(@PathVariable String bioguideId) {

        MemberEntity member = memberRepository.findByBioguideId(bioguideId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + bioguideId));

        if (member.getCurrentMember() != null && !member.getCurrentMember()) {
            throw new RuntimeException("Cannot sync historical member: " + bioguideId);
        }

        coordinator.syncOne(member);
        return "Sync started for " + bioguideId;
    }
}