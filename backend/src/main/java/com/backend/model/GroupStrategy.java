package com.backend.model;

/**
 * GroupStrategy.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Strategy enum defining all supported aggregation dimensions for time-tracking queries.
 */
public enum GroupStrategy {
    NONE,
    PROJECT,
    PROJECT_EMPLOYEE,
    EMPLOYEE_PROJECT
}
