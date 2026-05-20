package com.backend.model;

/**
 * Employee.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Domain model representing an employee enterprise entity using Java Record.
 */
public record Employee(Long id, String name) {
    // Immutable record fields: id (unique identifier) and name (employee first name)
}
