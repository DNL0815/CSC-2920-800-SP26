package com.csc2920.group_project.service;

import com.csc2920.group_project.dto.MemberDto;
import com.csc2920.group_project.dto.MemberResponse;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final String baseUrl;
    private final String apiKey;

    public MemberService(RestTemplate restTemplate,
                         MemberRepository memberRepository,
                         @Value("${congress.api.base-url}") String baseUrl,
                         @Value("${congress.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.memberRepository = memberRepository;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    // ---------------------------------------------------------
    // 1. PAGINATED RAW API CALL FOR ALL MEMBERS (current + former)
    // ---------------------------------------------------------
    public List<MemberDto> getAllMembers() {

        List<MemberDto> allMembers = new ArrayList<>();
        int offset = 0;
        int pageSize = 250;

        while (true) {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/member")
                    .queryParam("limit", pageSize)
                    .queryParam("offset", offset)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .toUriString();

            MemberResponse response = restTemplate.getForObject(url, MemberResponse.class);

            if (response == null || response.getMembers() == null || response.getMembers().isEmpty()) {
                break; // no more pages
            }

            allMembers.addAll(response.getMembers());

            offset += pageSize;
        }

        return allMembers;
    }

    // ---------------------------------------------------------
    // 2. RAW API CALL FOR MEMBER BY ID (shows currentMember true/false)
    // ---------------------------------------------------------
    public String getMemberByIdRaw(String memberId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/member/" + memberId)
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }

    // ---------------------------------------------------------
    // 3. SYNC ALL MEMBERS INTO DATABASE (PAGINATED)
    // ---------------------------------------------------------
    public void syncMembersToDatabase() {

        List<MemberDto> allMembers = getAllMembers();

        // Clear old data so DB always contains ALL members
        memberRepository.deleteAll();

        // Map DTO → Entity
        allMembers.forEach(dto -> {
            MemberEntity entity = new MemberEntity();
            entity.setBioguideId(dto.getBioguideId());
            entity.setName(dto.getName());
            entity.setPartyName(dto.getPartyName());
            entity.setState(dto.getState());
            entity.setDistrict(dto.getDistrict());

            // Extract chamber + startYear from first term
            if (dto.getTerms() != null &&
                    dto.getTerms().getItem() != null &&
                    !dto.getTerms().getItem().isEmpty()) {

                MemberDto.Term term = dto.getTerms().getItem().get(0);
                entity.setChamber(term.getChamber());
                entity.setStartYear(term.getStartYear());
            }

            // Depiction (image)
            if (dto.getDepiction() != null) {
                entity.setImageUrl(dto.getDepiction().getImageUrl());
            }

            entity.setUpdateDate(dto.getUpdateDate());

            memberRepository.save(entity);
        });
        // To show it has been SYNC
        System.out.println("Syncing has complete");
    }

    // ---------------------------------------------------------
    // 4. RAW API CALL: Sponsored Legislation for a Member
    // ---------------------------------------------------------
    public String getSponsoredLegislation(String bioguideId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/member/" + bioguideId + "/sponsored-legislation")
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .build()
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }

    // ---------------------------------------------------------
    // 5. RAW API CALL: Cosponsored Legislation for a Member
    // ---------------------------------------------------------
    public String getCosponsoredLegislation(String bioguideId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/member/" + bioguideId + "/cosponsored-legislation")
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", "250")
                .build()
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}
