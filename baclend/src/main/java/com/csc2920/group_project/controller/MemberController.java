package com.csc2920.group_project.controller;

import com.csc2920.group_project.dto.LegislationDto;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import com.csc2920.group_project.service.MemberLegislationService;
import com.csc2920.group_project.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final MemberLegislationService memberLegislationService;

    public MemberController(MemberService memberService,
                            MemberRepository memberRepository,
                            MemberLegislationService memberLegislationService) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.memberLegislationService = memberLegislationService;
    }

    // ---------------------------------------------------------
    // 1. DB: return ONLY current members
    // ---------------------------------------------------------
    @GetMapping("/db")
    public List<MemberEntity> getMembersFromDb() {
        return memberRepository.findByCurrentMemberTrue();
    }

    // ---------------------------------------------------------
    // 2. Trigger sync: fetch CURRENT members only
    // ---------------------------------------------------------
    @PostMapping("/sync")
    public String syncMembers() {
        memberService.syncMembersToDatabase();
        return "Database synced with CURRENT members only.";
    }

    // ---------------------------------------------------------
    // 3. All legislation for a member (DB-backed)
    // ---------------------------------------------------------
    @GetMapping("/{bioguideId}/legislation")
    public List<LegislationDto> getAllLegislation(@PathVariable String bioguideId) {
        return memberLegislationService.getAllForMember(bioguideId);
    }

    // ---------------------------------------------------------
    // 4. Sponsored only
    // ---------------------------------------------------------
    @GetMapping("/{bioguideId}/legislation/sponsored")
    public List<LegislationDto> getSponsored(@PathVariable String bioguideId) {
        return memberLegislationService.getSponsoredForMember(bioguideId);
    }

    // ---------------------------------------------------------
    // 5. Cosponsored only
    // ---------------------------------------------------------
    @GetMapping("/{bioguideId}/legislation/cosponsored")
    public List<LegislationDto> getCosponsored(@PathVariable String bioguideId) {
        return memberLegislationService.getCosponsoredForMember(bioguideId);
    }
}