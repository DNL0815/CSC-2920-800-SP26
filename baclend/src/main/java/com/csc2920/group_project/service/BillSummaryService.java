package com.csc2920.group_project.service;

import com.csc2920.group_project.dto.BillSummaryDto;
import com.csc2920.group_project.mapper.BillSummaryMapper;
import com.csc2920.group_project.repository.BillSummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillSummaryService {

    private final BillSummaryRepository billSummaryRepository;

    public BillSummaryService(BillSummaryRepository billSummaryRepository) {
        this.billSummaryRepository = billSummaryRepository;
    }

    public List<BillSummaryDto> getSummaries(Integer congress, String billType, String billNumber) {
        return billSummaryRepository
                .findByLegislation_CongressAndLegislation_BillTypeIgnoreCaseAndLegislation_BillNumberOrderByUpdateDateDesc(
                        congress,
                        billType,
                        billNumber
                )
                .stream()
                .map(BillSummaryMapper::toDto)
                .toList();
    }
}
