package com.csc2920.group_project.repository;

import com.csc2920.group_project.entity.LegislationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LegislationRepository extends JpaRepository<LegislationEntity, Long> {

    Optional<LegislationEntity> findByCongressAndBillTypeAndBillNumber(
            Integer congress,
            String billType,
            String billNumber
    );

    Optional<LegislationEntity> findByCongressAndBillTypeIgnoreCaseAndBillNumber(
            Integer congress,
            String billType,
            String billNumber
    );
}