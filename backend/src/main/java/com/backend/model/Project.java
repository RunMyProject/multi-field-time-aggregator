package com.backend.model;

/**
 * Project.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Domain model representing a project enterprise entity using Java Record.
 */
public record Project(Long id, String name) {
    // Immutable record fields: id (unique identifier) and name (project title)
}
