package com.backend.repository;

import com.backend.model.ActivityRegistry;
import com.backend.model.Employee;
import com.backend.model.EmployeeProjectHoursRow;
import com.backend.model.Project;
import com.backend.model.ProjectEmployeeHoursRow;
import com.backend.model.ProjectHoursRow;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Comparator;
/**
 * ActivityRegistryRepositoryInMemoryDBImpl.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Production-mimicking in-memory database engine implementing the ActivityRegistryRepository contract.
 */
@Component // Spring annotation to denote this class as a component, 
// enabling it to be auto-detected and injected where needed
public class ActivityRegistryRepositoryInMemoryDBImpl implements ActivityRegistryRepository {

    // Thread-safe immutable collection acting as the database table snapshot
    private final List<ActivityRegistry> activityRegistries;

    public ActivityRegistryRepositoryInMemoryDBImpl() {
        // Simulating Database Primary Keys and Tables via core Objects
        Project marsRover = new Project(1L, "Mars Rover");
        Project manhattan = new Project(2L, "Manhattan");

        Employee mario = new Employee(1L, "Mario");
        Employee giovanni = new Employee(2L, "Giovanni");
        Employee lucia = new Employee(3L, "Lucia");

        // Hardcoded relational mapping exactly matching the assessment requirements
        this.activityRegistries = List.of(
            new ActivityRegistry(marsRover, mario, ZonedDateTime.parse("2021-08-26T22:00:00.000Z"), 5),
            new ActivityRegistry(manhattan, giovanni, ZonedDateTime.parse("2021-08-30T22:00:00.000Z"), 3),
            new ActivityRegistry(marsRover, mario, ZonedDateTime.parse("2021-08-31T22:00:00.000Z"), 3),
            new ActivityRegistry(marsRover, lucia, ZonedDateTime.parse("2021-08-31T22:00:00.000Z"), 3),
            new ActivityRegistry(manhattan, mario, ZonedDateTime.parse("2021-08-26T22:00:00.000Z"), 2),
            new ActivityRegistry(manhattan, giovanni, ZonedDateTime.parse("2021-08-31T22:00:00.000Z"), 4)
        );
    }

    /**
     * Concrete execution of the JPA simulation lifecycle.
     * @return Immutable list of records.
     */
    @Override
    public List<ActivityRegistry> findAll() {
        // Directly serves the operational dataset to the injected service layer
        return this.activityRegistries;
    }

    /**
     * Grouping strictly by Project Name and summing hours.
     */
    @Override
    public List<ProjectHoursRow> findAllByProject() {
        return this.activityRegistries.stream()
                .collect(Collectors.groupingBy( // Grouping key: Project Name
                        activity -> activity.project().name(),  // Grouping value: Summed Hours
                        Collectors.summingInt(ActivityRegistry::hours)
                        // Downstream collector that sums the hours for each project group
                ))
                .entrySet().stream() // Convert the Map entries to a Stream for transformation
                .map(entry -> new ProjectHoursRow(entry.getKey(), entry.getValue()))
                // Map each entry to a ProjectHoursRow object, where entry.getKey() 
                // is the project name and entry.getValue() is the total hours
                .sorted(Comparator.comparing(ProjectHoursRow::totalHours, Comparator.reverseOrder())
                .thenComparing(ProjectHoursRow::projectName))
                .toList(); // From Java 16+, collects the Stream into an immutable List
    }

    /**
     * Multi-level grouping: Project Name -> Employee Name -> Summed Hours.
     */
    @Override
    public List<ProjectEmployeeHoursRow> findAllByProjectAndEmployee() {
        record QueryKey(String project, String employee) {}

        // Step 1: flat list aggregation - we create a flat list of 
        // ProjectEmployeeHoursRow where each row represents a unique combination of 
        // project and employee, along with the total hours for that combination. 
        // This is done by first grouping the activity registries by a composite key
        // of project name and employee name, summing the hours for each group, and 
        // then mapping the results to ProjectEmployeeHoursRow objects.
        List<ProjectEmployeeHoursRow> rows = this.activityRegistries.stream()
            .collect(Collectors.groupingBy(
                a -> new QueryKey(a.project().name(), a.employee().name()),
                // Grouping key: combination of Project Name and Employee Name
                Collectors.summingInt(ActivityRegistry::hours)
                // Downstream collector that sums the hours for each project-employee group
            ))
            // Here we have a flat Map where the key is our composite QueryKey and 
            // the value is the summed hours
            .entrySet().stream()
            .map(e -> new ProjectEmployeeHoursRow(
                        e.getKey().project(),
                        e.getKey().employee(),
                        e.getValue()
            ))
            .toList();

        // Step 2: max hours per project (determines which project comes first)
        Map<String, Integer> maxHoursByProject = rows.stream()
                .collect(Collectors.toMap( // it transforms the list of ProjectEmployeeHoursRow 
                // into a Map where the key is the project name 
                // and the value is the maximum hours for that project
                        ProjectEmployeeHoursRow::projectName,
                        ProjectEmployeeHoursRow::totalHours,
                        Integer::max   
                        // If there are multiple entries for the same project (different employees), 
                        // we take the maximum hours to determine the project's rank in the sorting phase
                ));

        // Step 3: sort for max project hours DESC → total hours DESC → employee name ASC
        return rows.stream()
                .sorted(
                Comparator.<ProjectEmployeeHoursRow, Integer>comparing(
                                r -> maxHoursByProject.get(r.projectName()), Comparator.reverseOrder()
                ) // First level of sorting: by the maximum hours of the project (descending)
                .thenComparing(
                        Comparator.<ProjectEmployeeHoursRow, Integer>comparing(
                                r -> r.totalHours(), Comparator.reverseOrder()
                        )
                ) // Second level of sorting: by the total hours for the employee within the project (descending)
                .thenComparing(r -> r.employeeName())
                )
        .toList();
    }

    /**
     * Multi-level grouping: Employee Name -> Project Name -> Summed Hours.
     */
    @Override
    public List<EmployeeProjectHoursRow> findAllByEmployeeAndProject() {
        record QueryKey(String employee, String project) {}

        // Step 1: flat list aggregation - create a flat list of 
        // EmployeeProjectHoursRow representing unique employee-project combinations.
        List<EmployeeProjectHoursRow> rows = this.activityRegistries.stream()
        .collect(Collectors.groupingBy( // Grouping key: combination of Project Name and Employee Name
                a -> new QueryKey(a.employee().name(), a.project().name()),
                Collectors.summingInt(ActivityRegistry::hours)
        )) 
        .entrySet().stream()
        .map(e -> new EmployeeProjectHoursRow(
                e.getKey().employee(),
                e.getKey().project(),
                e.getValue()
        ))
        .toList();

        // Step 2: max hours per employee (determines which employee comes first in sorting)
        Map<String, Integer> maxHoursByEmployee = rows.stream()
        .collect(Collectors.toMap(
                EmployeeProjectHoursRow::employeeName,
                EmployeeProjectHoursRow::totalHours,
                Integer::max // If there are multiple entries for the same employee
                // (different projects), we take the maximum hours to determine the employee's 
                // rank in the sorting phase
        ));

        // Step 3: sort for max employee hours DESC → total hours DESC → project name ASC
        return rows.stream()
        .sorted(
        Comparator.<EmployeeProjectHoursRow, Integer>comparing(
                r -> maxHoursByEmployee.get(r.employeeName()), Comparator.reverseOrder()
        ) // First level of sorting: by the maximum hours of the employee (descending)
                .thenComparing(
                        Comparator.<EmployeeProjectHoursRow, Integer>comparing(
                        EmployeeProjectHoursRow::totalHours, Comparator.reverseOrder()
                )
        ) // Second level of sorting: by the total hours for the project within the employee (descending)
                .thenComparing(EmployeeProjectHoursRow::projectName)
        )
        .toList();
    }
}
