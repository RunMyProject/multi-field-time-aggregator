package com.backend.service;

import com.backend.model.ActivityRegistry;
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
}
