package com.backend.repository;

import com.backend.model.ActivityRegistry;
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
}
