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
}