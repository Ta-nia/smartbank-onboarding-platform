package com.smartbank.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Route Configuration — defines which URLs map to which services.

 * Pattern:
 *   Client calls:  GET http://localhost:8080/api/onboarding/applications
 *   Gateway routes to: http://onboarding-service-host:8081/api/onboarding/applications
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Onboarding Service routes
                .route("onboarding-service", r -> r
                        .path("/api/onboarding/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("onboarding-cb")
                                        .setFallbackUri("forward:/fallback/onboarding"))
                        )
                        .uri("lb://smartbank-onboarding-service"))

                // KYC Service routes
                .route("kyc-service", r -> r
                        .path("/api/kyc/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("kyc-cb")
                                        .setFallbackUri("forward:/fallback/kyc"))
                        )
                        .uri("lb://smartbank-kyc-service"))

                // Notification Service routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("lb://smartbank-notification-service"))

                // Document Service routes
                .route("document-service", r -> r
                        .path("/api/documents/**")
                        .uri("lb://smartbank-document-service"))

                // Auth routes — public, no JWT needed
                .route("auth-route", r -> r
                        .path("/api/auth/**")
                        .uri("lb://smartbank-onboarding-service"))

                .build();
    }
}