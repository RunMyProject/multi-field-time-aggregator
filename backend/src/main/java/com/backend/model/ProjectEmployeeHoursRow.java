package com.backend.model;

/**
 * ProjectEmployeeHoursRow.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Projection DTO representing aggregated hours bucketed by project and employee.
 */
public record ProjectEmployeeHoursRow(String projectName, String employeeName, Integer totalHours) {}