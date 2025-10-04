package org.example.mizzyquiz.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.example.mizzyquiz.auth.dto.JwtAuthResponse;
import org.example.mizzyquiz.auth.dto.LoginRequest;
import org.example.mizzyquiz.auth.dto.RegisterRequest;
import org.example.mizzyquiz.auth.dto.UserDto;
import org.example.mizzyquiz.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) throws BadRequestException {
        UserDto user = authService.registerUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtAuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
