package com.ticketcompany.booking_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketcompany.booking_service.dto.LoginRequest;
import com.ticketcompany.booking_service.dto.LoginResponse;
import com.ticketcompany.booking_service.dto.RegisterRequest;
import com.ticketcompany.booking_service.service.AuthService;
import com.ticketcompany.booking_service.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.email(), request.password());
        return new LoginResponse(token, TokenService.TOKEN_LIFETIME.toSeconds());
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}