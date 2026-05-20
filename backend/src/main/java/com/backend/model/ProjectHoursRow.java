package com.backend.model;

/**
 * ProjectHoursRow.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Projection DTO representing aggregated hours bucketed by project.
 */
public record ProjectHoursRow(String projectName, Integer totalHours) {}
