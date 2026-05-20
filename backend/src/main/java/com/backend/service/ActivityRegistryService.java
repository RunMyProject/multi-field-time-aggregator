package com.backend.service;

import com.backend.model.ActivityRegistry;
import com.backend.model.EmployeeProjectHoursRow;
import com.backend.model.ProjectEmployeeHoursRow;
import com.backend.model.ProjectHoursRow;
import com.backend.repository.ActivityRegistryRepositoryInMemoryDBImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * ActivityRegistryService.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Business service layer orchestrated to process time tracking data structures.
 */
@Service // Spring annotation to denote this class as a service component in the application context
@RequiredArgsConstructor // Lombok annotation to generate a constructor for all final fields, 
// facilitating clean DI without boilerplate code
public class ActivityRegistryService {

    // Final dependency reference cleanly injected via Lombok @RequiredArgsConstructor
    private final ActivityRegistryRepositoryInMemoryDBImpl repository;

    /**
     * Retrieves the raw list of employee tracking entities from the lower data infrastructure layer.
     * @return List of unaggregated records.
     */
    public List<ActivityRegistry> getAllActivities() {
        // Execute operational pipeline call to the in-memory repository instance
        return repository.findAll();
    }

    /**
     * Service contract to retrieve hours aggregated strictly by Project.
     * @return List of ProjectHoursRow projections.
     */
    public List<ProjectHoursRow> getActivitiesGroupedByProject() {
        return repository.findAllByProject();
    }

    /**
     * Service contract to retrieve hours aggregated by Project and Employee.
     * @return List of ProjectEmployeeHoursRow projections.
     */
    public List<ProjectEmployeeHoursRow> getActivitiesGroupedByProjectAndEmployee() {
        return repository.findAllByProjectAndEmployee();
    }

    /**
     * Service contract to retrieve hours aggregated by Employee and Project.
     * @return List of EmployeeProjectHoursRow projections.
     */
    public List<EmployeeProjectHoursRow> getActivitiesGroupedByEmployeeAndProject() {
        return repository.findAllByEmployeeAndProject();
    }
}
