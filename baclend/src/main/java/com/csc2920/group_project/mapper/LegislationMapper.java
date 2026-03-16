package com.csc2920.group_project.mapper;

import com.csc2920.group_project.dto.LegislationDto;
import com.csc2920.group_project.entity.LegislationEntity;
import com.csc2920.group_project.entity.MemberLegislationEntity;

public class LegislationMapper {

    public static LegislationDto toDto(MemberLegislationEntity link) {
        if (link == null) return null;

        LegislationEntity e = link.getLegislation();

        return new LegislationDto(
                e.getCongress(),
                e.getBillType(),
                e.getBillNumber(),
                e.getIntroducedDate(),
                e.getTitle(),
                e.getPolicyArea(),
                e.getLatestActionDate(),
                e.getLatestActionText(),
                e.getUrl(),
                link.getSource() // <-- source comes from join entity
        );
    }
}