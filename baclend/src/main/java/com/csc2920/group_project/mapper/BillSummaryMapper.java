package com.csc2920.group_project.mapper;

import com.csc2920.group_project.dto.BillSummaryDto;
import com.csc2920.group_project.entity.BillSummaryEntity;

public class BillSummaryMapper {

    public static BillSummaryDto toDto(BillSummaryEntity e) {
        if (e == null) return null;
        return new BillSummaryDto(
                e.getActionDate(),
                e.getActionDesc(),
                e.getText(),
                e.getUpdateDate(),
                e.getVersionCode()
        );
    }
}
