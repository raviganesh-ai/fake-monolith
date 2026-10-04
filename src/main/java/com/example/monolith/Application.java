package com.example.monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for the modular monolith application.
 *
 * The application remains a single deployable Spring Boot service, while
 * internal packages are organized into feature-oriented modules (customers,
 * products, orders, web, and data initialization).
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
