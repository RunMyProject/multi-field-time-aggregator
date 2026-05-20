package com.backend.model;

import java.time.ZonedDateTime;

/**
 * ActivityRegistry.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Aggregate domain model representing a single time-tracking log entry.
 */
public record ActivityRegistry(Project project, Employee employee, ZonedDateTime date, Integer hours) {
    // Holds structural relationships between Project, Employee, and the physical logged hours
}