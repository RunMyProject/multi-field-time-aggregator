package com.backend.controller;

import com.backend.model.ActivityRegistry;
import com.backend.service.ActivityRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * ActivityRegistryController.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: REST routing endpoint engine exposing operational employee time metrics.
 */
@RestController // Spring annotation to designate this class as a REST controller, 
// enabling request handling and response generation
@RequestMapping(path = "/api") // Base path for all endpoints in this controller, 
// facilitating API versioning and modular route management
@RequiredArgsConstructor // Lombok annotation to generate a constructor for all final fields, 
// facilitating clean DI without boilerplate code
public class ActivityRegistryController {

    // Core business logic dependency layer cleanly injected via Lombok @RequiredArgsConstructor
    private final ActivityRegistryService activityRegistryService;

    /**
     * Endpoint targeting raw activity monitoring arrays.
     * @return ResponseEntity holding standard HTTP envelope and dataset.
     */
    @GetMapping(path = "/activities", version = "1.0") // NB: Version specified in the annotation for API versioning
    public ResponseEntity<List<ActivityRegistry>> getRawActivities() {

        // Extracts context parameters out from the service layout execution
        List<ActivityRegistry> data = activityRegistryService.getAllActivities();

        // Standardized corporate envelope tracking return: HTTP 200 OK
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(data);
    }
}
