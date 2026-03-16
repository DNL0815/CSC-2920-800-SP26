package com.csc2920.group_project.dto;

public record BillSummaryDto(
        String actionDate,
        String actionDesc,
        String text,
        String updateDate,
        String versionCode
) {}
