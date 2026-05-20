package com.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Controller for handling hello requests with versioning.
 */

@RestController
@RequestMapping(path = "/api")
public class HelloController {

    public HelloController() {}

    @GetMapping(path = "/hello", version = "1.0") // NB: Version specified in the annotation for API versioning
    public ResponseEntity<String> sayHello() {

        // Returns HTTP 200 OK with the body "Hello World!"
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Hello World!");
    }
}
