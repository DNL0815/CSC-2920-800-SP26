package com.csc2920.group_project.service;

import com.csc2920.group_project.dto.MemberDto;
import com.csc2920.group_project.dto.MemberResponse;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    private static final int TARGET_CURRENT_MEMBERS = 538;
    private static final int MEMBER_PAGE_SIZE = 250;
    private static final int TOP_LEGISLATION_LIMIT = 5;

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
    // SAFE GET WITH RETRY + BACKOFF (fixes 429, 520, 500, 503)
    // ---------------------------------------------------------
    private <T> T safeGet(String url, Class<T> clazz) {
        int retries = 5;
        int delay = 1000;  // Start at 1 second

        for (int i = 0; i < retries; i++) {
            try {
                T result = restTemplate.getForObject(url, clazz);
                if (i > 0) {
                    System.out.println("✅ SUCCESS after " + i + " retries: " + url);
                }
                return result;

            } catch (HttpClientErrorException.TooManyRequests e) {
                // 429 - Rate limit exceeded
                System.out.println("⚠️ 429 RATE LIMITED | Attempt " + (i+1) + "/" + retries +
                        " | Waiting " + delay + "ms before retry...");
                delay *= 2;  // Exponential backoff
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}

            } catch (HttpServerErrorException e) {
                // 5xx - Server errors (503, 502, 500, etc.)
                System.out.println("⚠️ " + e.getStatusCode() + " SERVER ERROR | Attempt " + (i+1) + "/" + retries +
                        " | Waiting " + delay + "ms before retry...");
                delay *= 2;  // Exponential backoff
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}

            } catch (ResourceAccessException e) {
                // Connection timeout, connection refused, etc.
                System.out.println("⚠️ CONNECTION ERROR | Attempt " + (i+1) + "/" + retries +
                        " | " + e.getMessage() + " | Waiting " + delay + "ms before retry...");
                delay *= 2;
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}

            } catch (Exception e) {
                // Unexpected errors (don't retry these)
                System.out.println("❌ UNEXPECTED ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        System.out.println("❌ FAILED after " + retries + " retries: " + url);
        return null;
    }

    // ---------------------------------------------------------
    // IMAGE URL NORMALIZATION
    // ---------------------------------------------------------
    private String normalizeImageUrl(String rawUrl) {
        if (rawUrl == null) return null;

        if (rawUrl.contains("bioguide.congress.gov/photo")) {
            int idx = rawUrl.indexOf("https://bioguide.congress.gov");
            return rawUrl.substring(idx);
        }

        if (rawUrl.startsWith("https://www.congress.gov/img/member/")) {
            return rawUrl;
        }

        return rawUrl;
    }

    // ---------------------------------------------------------
    // 1. FETCH CURRENT MEMBERS ONLY (list endpoint)
    //    - filter at API level to avoid historical members
    //    - cap at 538 to avoid extra paging/work
    // ---------------------------------------------------------
    public List<MemberDto> getAllMembers() {

        List<MemberDto> allMembers = new ArrayList<>();
        int offset = 0;

        while (allMembers.size() < TARGET_CURRENT_MEMBERS) {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/member")
                    .queryParam("currentMember", true)
                    .queryParam("limit", MEMBER_PAGE_SIZE)
                    .queryParam("offset", offset)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .toUriString();

            MemberResponse response = safeGet(url, MemberResponse.class);

            if (response == null || response.getMembers() == null || response.getMembers().isEmpty()) {
                break;
            }

            allMembers.addAll(response.getMembers());
            offset += MEMBER_PAGE_SIZE;
        }

        if (allMembers.size() > TARGET_CURRENT_MEMBERS) {
            return new ArrayList<>(allMembers.subList(0, TARGET_CURRENT_MEMBERS));
        }

        return allMembers;
    }

    // ---------------------------------------------------------
    // 2. SYNC MEMBERS INTO DATABASE (CURRENT MEMBERS ONLY)
    //    Stops immediately when DB already has all 538.
    // ---------------------------------------------------------
    public void syncMembersToDatabase() {

        long existingCurrent = memberRepository.countByCurrentMemberTrue();
        if (existingCurrent >= TARGET_CURRENT_MEMBERS) {
            System.out.println("Current members already populated (" + existingCurrent + "). Skipping member sync.");
            return;
        }

        List<MemberDto> currentMembers = getAllMembers();

        for (MemberDto dto : currentMembers) {
            if (dto == null || dto.getBioguideId() == null || dto.getName() == null) {
                System.out.println("Skipping malformed member: " + dto);
                continue;
            }

            MemberEntity entity = new MemberEntity();
            entity.setBioguideId(dto.getBioguideId());
            entity.setName(dto.getName());
            entity.setPartyName(dto.getPartyName());
            entity.setState(dto.getState());
            entity.setDistrict(dto.getDistrict());
            entity.setUpdateDate(dto.getUpdateDate());
            entity.setCurrentMember(true);

            if (dto.getTerms() != null &&
                    dto.getTerms().getItem() != null &&
                    !dto.getTerms().getItem().isEmpty()) {

                MemberDto.Term term = dto.getTerms().getItem().get(0);
                entity.setChamber(term.getChamber());
                entity.setStartYear(term.getStartYear());
            }

            if (dto.getDepiction() != null) {
                String cleaned = normalizeImageUrl(dto.getDepiction().getImageUrl());
                entity.setImageUrl(cleaned);
            }

            memberRepository.save(entity);
        }

        long after = memberRepository.countByCurrentMemberTrue();
        System.out.println("Member sync complete. Current members in DB: " + after);
    }

    // ---------------------------------------------------------
    // 3. SPONSORED LEGISLATION (TOP 5 ONLY)
    // ---------------------------------------------------------
    public List<Map<String, Object>> getSponsoredLegislation(String bioguideId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/member/" + bioguideId + "/sponsored-legislation")
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", TOP_LEGISLATION_LIMIT)
                .queryParam("offset", 0)
                .toUriString();

        Map<String, Object> json = safeGet(url, Map.class);
        if (json == null) return Collections.emptyList();

        List<Map<String, Object>> page = (List<Map<String, Object>>) json.get("sponsoredLegislation");
        return page != null ? page : Collections.emptyList();
    }

    // ---------------------------------------------------------
    // 4. COSPONSORED LEGISLATION (TOP 5 ONLY)
    // ---------------------------------------------------------
    public List<Map<String, Object>> getCosponsoredLegislation(String bioguideId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/member/" + bioguideId + "/cosponsored-legislation")
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", TOP_LEGISLATION_LIMIT)
                .queryParam("offset", 0)
                .toUriString();

        Map<String, Object> json = safeGet(url, Map.class);
        if (json == null) return Collections.emptyList();

        List<Map<String, Object>> page = (List<Map<String, Object>>) json.get("cosponsoredLegislation");
        return page != null ? page : Collections.emptyList();
    }
}