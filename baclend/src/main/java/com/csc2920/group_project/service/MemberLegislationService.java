package com.csc2920.group_project.service;

import com.csc2920.group_project.dto.LegislationDto;
import com.csc2920.group_project.entity.MemberLegislationEntity;
import com.csc2920.group_project.mapper.LegislationMapper;
import com.csc2920.group_project.repository.MemberLegislationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberLegislationService {

    private final MemberLegislationRepository memberLegislationRepository;

    public MemberLegislationService(MemberLegislationRepository memberLegislationRepository) {
        this.memberLegislationRepository = memberLegislationRepository;
    }

    public List<LegislationDto> getAllForMember(String bioguideId) {
        return memberLegislationRepository.findByMember_BioguideId(bioguideId)
                .stream()
                .map(LegislationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LegislationDto> getSponsoredForMember(String bioguideId) {
        return memberLegislationRepository.findByMember_BioguideIdAndSource(bioguideId, "sponsored")
                .stream()
                .map(LegislationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LegislationDto> getCosponsoredForMember(String bioguideId) {
        return memberLegislationRepository.findByMember_BioguideIdAndSource(bioguideId, "cosponsored")
                .stream()
                .map(LegislationMapper::toDto)
                .collect(Collectors.toList());
    }
}