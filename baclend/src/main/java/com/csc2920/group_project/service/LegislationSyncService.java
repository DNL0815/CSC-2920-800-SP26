package com.csc2920.group_project.service;

import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.entity.MemberLegislationEntity;
import com.csc2920.group_project.repository.LegislationRepository;
import com.csc2920.group_project.repository.MemberLegislationRepository;
import com.csc2920.group_project.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LegislationSyncService {

    private final MemberService memberService;
    private final LegislationRepository legislationRepository;
    private final MemberRepository memberRepository;
    private final MemberLegislationRepository memberLegislationRepository;

    public LegislationSyncService(MemberService memberService,
                                  LegislationRepository legislationRepository,
                                  MemberRepository memberRepository,
                                  MemberLegislationRepository memberLegislationRepository) {
        this.memberService = memberService;
        this.legislationRepository = legislationRepository;
        this.memberRepository = memberRepository;
        this.memberLegislationRepository = memberLegislationRepository;
    }

    public void syncLegislationForMember(MemberEntity member) {

        member.getMemberLegislation().size(); // initialize lazy collection

        try {
            List<Map<String, Object>> sponsored =
                    memberService.getSponsoredLegislation(member.getBioguideId());

            List<Map<String, Object>> cosponsored =
                    memberService.getCosponsoredLegislation(member.getBioguideId());

            List<MemberLegislationEntity> newLinks = new ArrayList<>();

            buildTop5Links(sponsored, "sponsored", member, newLinks);
            buildTop5Links(cosponsored, "cosponsored", member, newLinks);

            if (!newLinks.isEmpty()) {
                memberLegislationRepository.deleteAll(member.getMemberLegislation());
                member.getMemberLegislation().clear();
                
                for (MemberLegislationEntity link : newLinks) {
                    memberLegislationRepository.save(link);
                }
                memberLegislationRepository.flush();
            }

        } catch (Exception e) {
            System.out.println("Error syncing member " + member.getBioguideId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buildTop5Links(List<Map<String, Object>> bills,
                                 String source,
                                 MemberEntity member,
                                 List<MemberLegislationEntity> output) {

        if (bills == null || bills.isEmpty()) {
            return;
        }

        bills.sort((a, b) -> {
            Map<String, Object> laA = (Map<String, Object>) a.get("latestAction");
            Map<String, Object> laB = (Map<String, Object>) b.get("latestAction");

            String dateA = laA != null ? (String) laA.get("actionDate") : null;
            String dateB = laB != null ? (String) laB.get("actionDate") : null;

            if (dateA == null && dateB == null) return 0;
            if (dateA == null) return 1;
            if (dateB == null) return -1;

            return dateB.compareTo(dateA);
        });

        List<Map<String, Object>> top5 =
                bills.size() > 5 ? bills.subList(0, 5) : bills;

        for (Map<String, Object> bill : top5) {

            String number = (String) bill.get("number");
            String type = (String) bill.get("type");
            Integer congress = (Integer) bill.get("congress");

            if (number == null || type == null || congress == null) {
                continue;
            }

            LegislationEntity legislation =
                    legislationRepository.findByCongressAndBillTypeAndBillNumber(congress, type, number)
                            .orElseGet(() -> createLegislation(bill, congress, type, number));

            MemberLegislationEntity link = new MemberLegislationEntity();
            link.setMember(member);
            link.setLegislation(legislation);
            link.setSource(source);

            output.add(link);
        }
    }

    private LegislationEntity createLegislation(Map<String, Object> bill,
                                                Integer congress,
                                                String type,
                                                String number) {

        LegislationEntity e = new LegislationEntity();
        e.setCongress(congress);
        e.setBillType(type);
        e.setBillNumber(number);

        e.setIntroducedDate((String) bill.get("introducedDate"));
        e.setTitle((String) bill.get("title"));
        e.setUrl((String) bill.get("url"));

        Map<String, Object> pa = (Map<String, Object>) bill.get("policyArea");
        e.setPolicyArea(pa != null ? (String) pa.get("name") : null);

        Map<String, Object> la = (Map<String, Object>) bill.get("latestAction");
        if (la != null) {
            e.setLatestActionDate((String) la.get("actionDate"));
            e.setLatestActionText((String) la.get("text"));
        }

        return legislationRepository.save(e);
    }
}