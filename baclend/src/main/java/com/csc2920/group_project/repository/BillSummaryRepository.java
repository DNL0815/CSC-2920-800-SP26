package com.csc2920.group_project.repository;

import com.csc2920.group_project.entity.BillSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillSummaryRepository extends JpaRepository<BillSummaryEntity, Long> {

    List<BillSummaryEntity> findByLegislation_CongressAndLegislation_BillTypeAndLegislation_BillNumberOrderByUpdateDateDesc(
            Integer congress,
            String billType,
            String billNumber
    );

    List<BillSummaryEntity> findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
            Integer congress,
            String billType,
            String billNumber
    );

    void deleteByLegislation_Id(Long legislationId);
}
