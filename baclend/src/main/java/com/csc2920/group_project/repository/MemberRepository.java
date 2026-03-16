package com.csc2920.group_project.repository;


import com.csc2920.group_project.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    Optional<MemberEntity> findByBioguideId(String bioguideId);

    List<MemberEntity> findByCurrentMemberTrue();

    long countByCurrentMemberTrue();
}

