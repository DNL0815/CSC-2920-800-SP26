package com.csc2920.group_project.dto;

public record LegislationDto(
        Integer congress,
        String billType,
        String billNumber,
        String introducedDate,
        String title,
        String policyArea,
        String latestActionDate,
        String latestActionText,
        String url,
        String source
) {}