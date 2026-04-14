package com.csc2920.group_project.service;

import com.csc2920.group_project.dto.LegislationDto;
import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.entity.MemberEntity;
import com.csc2920.group_project.entity.MemberLegislationEntity;
import com.csc2920.group_project.mapper.LegislationMapper;
import com.csc2920.group_project.repository.MemberLegislationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberLegislationService Tests")
class MemberLegislationServiceTest {

    @Mock
    private MemberLegislationRepository memberLegislationRepository;

    private MemberLegislationService memberLegislationService;

    @BeforeEach
    void setUp() {
        memberLegislationService = new MemberLegislationService(memberLegislationRepository);
    }

    // ============ getAllForMember() Tests ============

    @Test
    @DisplayName("getAllForMember returns all legislation for member")
    void testGetAllForMemberSuccess() {
        String bioguideId = "B001234";
        List<MemberLegislationEntity> mockEntities = createMockMemberLegislationEntities(3, bioguideId);
        
        when(memberLegislationRepository.findByMember_BioguideId(bioguideId))
            .thenReturn(mockEntities);

        List<LegislationDto> result = memberLegislationService.getAllForMember(bioguideId);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(memberLegislationRepository).findByMember_BioguideId(bioguideId);
    }

    @Test
    @DisplayName("getAllForMember returns empty list when no legislation found")
    void testGetAllForMemberEmpty() {
        String bioguideId = "B001234";
        when(memberLegislationRepository.findByMember_BioguideId(bioguideId))
            .thenReturn(Collections.emptyList());

        List<LegislationDto> result = memberLegislationService.getAllForMember(bioguideId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(memberLegislationRepository).findByMember_BioguideId(bioguideId);
    }

    @Test
    @DisplayName("getAllForMember handles null bioguideId gracefully")
    void testGetAllForMemberNullId() {
        when(memberLegislationRepository.findByMember_BioguideId(null))
            .thenReturn(Collections.emptyList());

        List<LegislationDto> result = memberLegislationService.getAllForMember(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ============ getSponsoredForMember() Tests ============

    @Test
    @DisplayName("getSponsoredForMember returns only sponsored legislation")
    void testGetSponsoredForMemberSuccess() {
        String bioguideId = "B001234";
        List<MemberLegislationEntity> mockEntities = createMockMemberLegislationEntities(2, bioguideId);
        
        when(memberLegislationRepository.findByMember_BioguideIdAndSource(bioguideId, "sponsored"))
            .thenReturn(mockEntities);

        List<LegislationDto> result = memberLegislationService.getSponsoredForMember(bioguideId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(memberLegislationRepository).findByMember_BioguideIdAndSource(bioguideId, "sponsored");
    }

    @Test
    @DisplayName("getSponsoredForMember returns empty list when no sponsored legislation found")
    void testGetSponsoredForMemberEmpty() {
        String bioguideId = "B001234";
        when(memberLegislationRepository.findByMember_BioguideIdAndSource(bioguideId, "sponsored"))
            .thenReturn(Collections.emptyList());

        List<LegislationDto> result = memberLegislationService.getSponsoredForMember(bioguideId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ============ getCosponsoredForMember() Tests ============

    @Test
    @DisplayName("getCosponsoredForMember returns only cosponsored legislation")
    void testGetCosponsoredForMemberSuccess() {
        String bioguideId = "B001234";
        List<MemberLegislationEntity> mockEntities = createMockMemberLegislationEntities(1, bioguideId);
        
        when(memberLegislationRepository.findByMember_BioguideIdAndSource(bioguideId, "cosponsored"))
            .thenReturn(mockEntities);

        List<LegislationDto> result = memberLegislationService.getCosponsoredForMember(bioguideId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberLegislationRepository).findByMember_BioguideIdAndSource(bioguideId, "cosponsored");
    }

    @Test
    @DisplayName("getCosponsoredForMember returns empty list when no cosponsored legislation found")
    void testGetCosponsoredForMemberEmpty() {
        String bioguideId = "B001234";
        when(memberLegislationRepository.findByMember_BioguideIdAndSource(bioguideId, "cosponsored"))
            .thenReturn(Collections.emptyList());

        List<LegislationDto> result = memberLegislationService.getCosponsoredForMember(bioguideId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ============ Helper Methods ============

    private List<MemberLegislationEntity> createMockMemberLegislationEntities(int count, String bioguideId) {
        List<MemberLegislationEntity> entities = new java.util.ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            MemberLegislationEntity entity = new MemberLegislationEntity();
            
            MemberEntity member = new MemberEntity();
            member.setBioguideId(bioguideId);
            entity.setMember(member);
            
            LegislationEntity legislation = new LegislationEntity();
            legislation.setId((long) i);
            legislation.setCongress(118);
            legislation.setBillType("HR");
            legislation.setBillNumber("" + (1000 + i));
            legislation.setTitle("Test Bill " + i);
            entity.setLegislation(legislation);
            
            entity.setSource(i % 2 == 0 ? "sponsored" : "cosponsored");
            
            entities.add(entity);
        }
        
        return entities;
    }
}
