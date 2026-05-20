package com.backend.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * ActivityRegistry.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Aggregate domain model representing a single time-tracking log entry.
 */
public record ActivityRegistry(
    Project project, 
    Employee employee, 
    // Custom JSON formatting to ensure consistent date representation in API responses
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Europe/Rome", locale = "en")
    ZonedDateTime date, 
    Integer hours) {
    // Holds structural relationships between Project, Employee, and the physical logged hours
}
