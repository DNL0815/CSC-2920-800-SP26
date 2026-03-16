package com.csc2920.group_project.repository;

import com.csc2920.group_project.entity.MemberLegislationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberLegislationRepository extends JpaRepository<MemberLegislationEntity, Long> {

    List<MemberLegislationEntity> findByMember_BioguideId(String bioguideId);

    List<MemberLegislationEntity> findByMember_BioguideIdAndSource(String bioguideId, String source);
}