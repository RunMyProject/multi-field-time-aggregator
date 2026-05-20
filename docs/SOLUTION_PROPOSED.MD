# `SOLUTION_PROPOSED.MD`

## 📅 Session 3: Wednesday, May 20, 2026

**Objective:** Architectural refactoring, implementation of multi-field aggregation logic and API endpoint hardening.

| Timestamp | Category | Action | Notes |
| --- | --- | --- | --- |
| **14:38** | **Brainstorming** | **Agile/Scrum Sync** | Collaborative session defining the aggregation strategy and sorting requirements. |
| **15:08** | **Refactor** | **Date Formatting** | Standardized JSON date serialization via `@JsonFormat`. |
| **15:30** | **Design** | **Model Extensibility** | Created `GroupStrategy` enum and new projection records. |
| **16:00** | **Development** | **Service Logic** | Implemented 3-step Stream pipeline for sorting complex aggregates. |
| **16:30** | **Deployment** | **Controller/API** | Implemented Switch Expressions for dynamic routing. |
| **16:50** | **Testing** | **Validation** | Updated Integration Tests and Bash Automation (`v2.0`). |
| **17:20** | **Docs** | **Documentation Updated** | Updated README and internal logs with the current file structure and testing protocols. |

---

## 🚀 Key Architectural Decisions

### 1. Date Serialization Standardization

To ensure consistency across the API, we applied a global format to the `date` field in our models:

```java
// Custom JSON formatting to ensure consistent date representation in API responses
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Europe/Rome", locale = "en")
private ZonedDateTime date;

```

### 2. Strategy Pattern for Aggregation

To cleanly handle multiple aggregation dimensions (None, Project, Project-Employee, Employee-Project), we introduced a `GroupStrategy` enum used by the controller to route traffic.

```java
package com.backend.model;

public enum GroupStrategy {
    NONE,
    PROJECT,
    PROJECT_EMPLOYEE,
    EMPLOYEE_PROJECT
}

```

This strategy is utilized in the `ActivityRegistryController` using modern Java 14+ **Switch Expressions**, which provide a concise and readable branching structure:

```java
@GetMapping(path = "/activities", version = "2.0")
public ResponseEntity<?> getActivities(
        @RequestParam(value = "groupBy", required = false, defaultValue = "NONE") GroupStrategy groupBy
) {
    // NB: the Switch Expression is a Java 14+ feature
    return switch (groupBy) {
        case NONE -> ResponseEntity.status(HttpStatus.OK).body(activityRegistryService.getAllActivities());
        case PROJECT -> ResponseEntity.status(HttpStatus.OK).body(activityRegistryService.getActivitiesGroupedByProject());
        case PROJECT_EMPLOYEE -> ResponseEntity.status(HttpStatus.OK).body(activityRegistryService.getActivitiesGroupedByProjectAndEmployee());
        case EMPLOYEE_PROJECT -> ResponseEntity.status(HttpStatus.OK).body(activityRegistryService.getActivitiesGroupedByEmployeeAndProject());
    };
}

```

---

## 🧠 The "Why": The 3-Step Stream Pipeline

During the implementation of `findAllByProjectAndEmployee` (and the inverse), we identified that a single stream operation was insufficient.

**The Challenge:** The requirement is to sort the data not just by the individual row's hours, but by the **total hours of the grouping entity (e.g., the Project)**. A single `Collectors.groupingBy` combined with a `Comparator` inside the stream fails because the comparator lacks visibility into the *aggregated sum* of the grouping group while processing individual rows.

**The Solution (3-Step Process):**

1. **Step 1: Flattening & Aggregation.** We use `groupingBy` to compress the raw activity list into a list of unique `Row` objects with their `totalHours` calculated.
2. **Step 2: Weight Calculation.** We create a secondary `Map<String, Integer>` (the "Weight" map) that maps the Grouping Key (e.g., Project Name) to the *total sum of hours* for that specific group. This gives us the context needed for sorting.
3. **Step 3: Sorting.** We use the "Weight" map created in Step 2 as the primary key in our `Comparator`. This forces the list to respect the hierarchy: **Group Total DESC -> Row Total DESC -> Name ASC**.

```java
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

```

---

## 📄 Repository Interface Contract

```java
/**
 * ActivityRegistryRepository.java
 * Defines data access layer mimicking Spring Data JPA conventions.
 */
public interface ActivityRegistryRepository {
    List<ActivityRegistry> findAll();
    List<ProjectHoursRow> findAllByProject();
    List<ProjectEmployeeHoursRow> findAllByProjectAndEmployee();
    List<EmployeeProjectHoursRow> findAllByEmployeeAndProject();
}

```

---

## ✅ Testing & Validation

### Integration Test

The `ActivityRegistryControllerIntegrationTest` was updated to verify that the `X-API-Version: 2.0` header is handled correctly and that the grouping logic returns the expected JSON payload size.

### Bash Automation: `ops/test_activities_v2.sh`

The test script was upgraded to handle the new `v2.0` API and dynamically parse the payload. By using a Python one-liner, we ensure that nested objects (like `project` and `employee`) are rendered as strings (`name`) rather than raw JSON, ensuring the terminal output remains readable and clean.

---

*Session finalized. Workspace configuration for `v2.0` aggregation logic is confirmed stable.*

