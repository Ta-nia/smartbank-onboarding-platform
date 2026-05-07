package com.smartbank.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartbank.common.dto.ApiResponse;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> onboardingFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(
                        "Onboarding service is temporarily unavailable. Please try again later.",
                        "SERVICE_UNAVAILABLE"
                ));
    }

    @GetMapping("/kyc")
    public ResponseEntity<ApiResponse<Void>> kycFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(
                        "KYC service is temporarily unavailable. Please try again later.",
                        "SERVICE_UNAVAILABLE"
                ));
    }
}