package com.smartbank.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Global filter — runs on EVERY request before it reaches any service.
 *
 * This is the Security pattern implementation:
 * 1. Check if the route is public (login, register) — if so, skip auth
 * 2. Check if Authorization header is present
 * 3. Validate the JWT token
 * 4. If valid → pass username downstream in a header
 * 5. If invalid → return 401 immediately, never reach the service
 *
 * Implements GlobalFilter so it applies to all routes automatically.
 * Implements Ordered so we control when it runs relative to other filters.
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    // These routes do not require authentication
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip auth for public routes
        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        // Check Authorization header exists
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract and validate token
        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Token is valid — extract username and pass it downstream
        // Services can read X-Auth-User header to know who is calling
        String username = jwtUtil.extractUsername(token);
        exchange.getRequest().mutate()
                .header("X-Auth-User", username)
                .build();

        return chain.filter(exchange);
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        // -1 means this filter runs before all other filters
        return -1;
    }
}