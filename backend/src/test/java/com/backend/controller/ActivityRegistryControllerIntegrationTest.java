package com.backend.controller;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.junit.jupiter.api.Assertions;
/**
 * ActivityRegistryControllerIntegrationTest.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Integration tests for ActivityRegistryController, verifying JSON list payloads and version validation.
 */

// 1. Core Framework Context & Environment
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

// 2. Web Layer Auto-Configurations
@AutoConfigureMockMvc
@AutoConfigureRestTestClient

// 3. Dependency Injection & Boilerplate Mechanics
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class ActivityRegistryControllerIntegrationTest {

    // Cleanly injected via constructor thanks to Lombok and @TestConstructor
    private final RestTestClient restTestClient;

    // TEST 0: Baseline validation of API versioning and JSON response structure
    @Test
    public void shouldReturn200AndFullActivitiesList() {
        // Tests successful JSON array response when the correct API version header is provided
        restTestClient.get()
                .uri("/api/activities")
                .header("X-API-Version", "1.0") // Crucial for triggering the versioned endpoint mapping
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                // Verifies that the JSON array returns the exact 6 mock entries from the InMemoryDB
                .expectBody()
                .jsonPath("$.length()").isEqualTo(6)
                .jsonPath("$[0].project.name").isEqualTo("Mars Rover")
                .jsonPath("$[0].employee.name").isEqualTo("Mario")
                .jsonPath("$[0].hours").isEqualTo(5);
    }

    // TEST 1: Baseline validation of API versioning and JSON response structure
    @Test
    public void shouldReturn200AndFullActivitiesListNoGrouped_v2() {
        // Tests successful JSON array response when the correct API version header is provided
        restTestClient.get()
                .uri("/api/activities")
                .header("X-API-Version", "2.0") // Crucial for triggering the versioned endpoint mapping
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                // Verifies that the JSON array returns the exact 6 mock entries from the InMemoryDB
                .expectBody()
                .jsonPath("$.length()").isEqualTo(6)
                .jsonPath("$[0].project.name").isEqualTo("Mars Rover")
                .jsonPath("$[0].employee.name").isEqualTo("Mario")
                .jsonPath("$[0].hours").isEqualTo(5);
    }

    // TEST 2: Aggregation strictly by PROJECT
    @Test
    public void shouldReturn200AndProjectRows_WhenGroupByProject() {
        restTestClient.get()
                .uri("/api/activities?groupBy=PROJECT")
                .header("X-API-Version", "2.0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").value(len -> Assertions.assertTrue((Integer) len > 0))
                .jsonPath("$[0].projectName").exists()
                .jsonPath("$[0].totalHours").exists()
                .jsonPath("$[0].employeeName").doesNotExist();
    }

    // TEST 3: Aggregation by PROJECT_EMPLOYEE
    @Test
    public void shouldReturn200AndProjectEmployeeRows_WhenGroupByProjectEmployee() {
        restTestClient.get()
                .uri("/api/activities?groupBy=PROJECT_EMPLOYEE")
                .header("X-API-Version", "2.0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").value(len -> Assertions.assertTrue((Integer) len > 0))
                .jsonPath("$[0].projectName").exists()
                .jsonPath("$[0].employeeName").exists()
                .jsonPath("$[0].totalHours").exists();
    }

    // TEST 4: Aggregation by EMPLOYEE_PROJECT
    @Test
    public void shouldReturn200AndEmployeeProjectRows_WhenGroupByEmployeeProject() {
        restTestClient.get()
                .uri("/api/activities?groupBy=EMPLOYEE_PROJECT")
                .header("X-API-Version", "2.0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").value(len -> Assertions.assertTrue((Integer) len > 0))
                .jsonPath("$[0].employeeName").exists()
                .jsonPath("$[0].projectName").exists()
                .jsonPath("$[0].totalHours").exists();
    }
}
