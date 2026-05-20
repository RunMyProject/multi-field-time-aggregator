package com.backend.repository;

import com.backend.model.ActivityRegistry;
import com.backend.model.EmployeeProjectHoursRow;
import com.backend.model.ProjectEmployeeHoursRow;
import com.backend.model.ProjectHoursRow;
import java.util.List;

/**
 * ActivityRegistryRepository.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Data access layer interface mimicking Spring Data JPA naming conventions for the ActivityRegistry entity.
 */
public interface ActivityRegistryRepository {

    /**
     * Standard JPA contract method to retrieve all rows/records from the data source.
     * @return List of ActivityRegistry entities.
     */
    List<ActivityRegistry> findAll();

    /**
     * Aggregates total hours bucketed strictly by Project.
     * @return List of ProjectHoursRow projections.
     */
    List<ProjectHoursRow> findAllByProject();

    /**
     * Aggregates total hours bucketed by Project and then by Employee.
     * @return List of ProjectEmployeeHoursRow projections.
     */
    List<ProjectEmployeeHoursRow> findAllByProjectAndEmployee();

    /**
     * Aggregates total hours bucketed by Employee and then by Project.
     * @return List of EmployeeProjectHoursRow projections.
     */
    List<EmployeeProjectHoursRow> findAllByEmployeeAndProject();
}
