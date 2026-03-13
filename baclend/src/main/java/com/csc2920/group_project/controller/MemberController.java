package com.csc2920.group_project.controller;

import com.csc2920.group_project.dto.MemberDto;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import com.csc2920.group_project.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public MemberController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    // ---------------------------------------------------------
    // 1. RAW member by ID (shows currentMember true/false)
    // ---------------------------------------------------------
    @GetMapping("/raw/{bioguideId}")
    public String getMemberByIdRaw(@PathVariable String bioguideId) {
        return memberService.getMemberByIdRaw(bioguideId);
    }

    // ---------------------------------------------------------
    // 2. DB: return ALL members stored in MySQL
    // ---------------------------------------------------------
    @GetMapping("/db")
    public List<MemberEntity> getMembersFromDb() {
        return memberRepository.findAll();
    }

    // ---------------------------------------------------------
    // 3. Trigger sync: fetch ALL members (paginated) and store in DB
    // ---------------------------------------------------------
    @PostMapping("/sync")
    public String syncMembers() {
        memberService.syncMembersToDatabase();
        return "Database synced with ALL members.";
    }

    // ---------------------------------------------------------
    // 4. Combined endpoint: sponsored + cosponsored merged
    // ---------------------------------------------------------
    @GetMapping("/{bioguideId}/legislation")
    public Map<String, Object> getMergedLegislation(@PathVariable String bioguideId) {

        String sponsoredRaw = memberService.getSponsoredLegislation(bioguideId);
        String cosponsoredRaw = memberService.getCosponsoredLegislation(bioguideId);

        List<Map<String, Object>> mergedList = new ArrayList<>();

        try {
            // Sponsored
            Map<String, Object> sponsoredJson = mapper.readValue(sponsoredRaw, Map.class);
            Object sponsoredBlock = sponsoredJson.get("sponsoredLegislation");

            if (sponsoredBlock instanceof List<?> list) {
                for (Object item : list) {
                    Map<String, Object> bill = (Map<String, Object>) item;
                    bill.put("source", "sponsored");
                    mergedList.add(bill);
                }
            }

            // Cosponsored
            Map<String, Object> cosponsoredJson = mapper.readValue(cosponsoredRaw, Map.class);
            Object cosponsoredBlock = cosponsoredJson.get("cosponsoredLegislation");

            if (cosponsoredBlock instanceof List<?> list) {
                for (Object item : list) {
                    Map<String, Object> bill = (Map<String, Object>) item;
                    bill.put("source", "cosponsored");
                    mergedList.add(bill);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse legislation JSON", e);
        }

        // Sort newest → oldest
        mergedList.sort((a, b) -> {
            String dateA = (String) a.getOrDefault("introducedDate", "");
            String dateB = (String) b.getOrDefault("introducedDate", "");
            return dateB.compareTo(dateA);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("bioguideId", bioguideId);
        response.put("legislation", mergedList);

        return response;
    }
}
