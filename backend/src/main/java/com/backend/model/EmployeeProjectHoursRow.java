package com.backend.model;

/**
 * EmployeeProjectHoursRow.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Projection DTO representing aggregated hours bucketed by employee and project.
 */
public record EmployeeProjectHoursRow(String employeeName, String projectName, Integer totalHours) {}
