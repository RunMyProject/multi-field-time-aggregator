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
 * HelloControllerIntegrationTest.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Integration tests for HelloController, verifying correct API version handling and response content.
 */

// 1. Core Framework Context & Environment
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Start the full application context on a random port
//  for true integration testing
@ActiveProfiles("test") // Use a dedicated 'test' profile to isolate test configurations and properties

// 2. Web Layer Auto-Configurations
@AutoConfigureMockMvc // Auto-configures MockMvc for testing the web layer without starting a real server
@AutoConfigureRestTestClient // Auto-configures RestTestClient for testing REST endpoints

// 3. Dependency Injection & Boilerplate Mechanics
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // Enables constructor injection for test classes, 
// allowing clean and explicit dependency management
@RequiredArgsConstructor // Lombok annotation to generate a constructor for all final fields, 
// facilitating clean DI without boilerplate code
public class HelloControllerIntegrationTest {

    // Cleanly injected via constructor thanks to Lombok and @TestConstructor
    private final RestTestClient restTestClient;

    @Test
    public void shouldReturn200AndHelloWorld() {
        // Tests successful response when the correct API version header is provided
        restTestClient.get()
                .uri("/api/hello")
                .header("X-API-Version", "1.0") // NB: This header is crucial for triggering the 
                // correct versioned endpoint in the controller
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World!");
    }

    @Test
    public void shouldReturn400WhenVersionIsWrong() {
        // Tests that an unhandled or incorrect API version returns 400 Bad Request
        restTestClient.get()
                .uri("/api/hello")
                .header("X-API-Version", "2.0") // NB: This header simulates a client requesting
                //  an unsupported API version, which should trigger the controller's versioning mechanism to reject the request
                .exchange()
                .expectStatus().isBadRequest();
    }
}
