package com.backend.repository;

import com.backend.model.ActivityRegistry;
import com.backend.model.Employee;
import com.backend.model.Project;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

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
}
