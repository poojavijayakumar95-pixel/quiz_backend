package com.quizapp.quiz.controller;



import com.quizapp.quiz.dto.AuthenticationRequest;
import com.quizapp.quiz.dto.AuthenticationResponse;
import com.quizapp.quiz.dto.RegisterRequest;
import com.quizapp.quiz.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
// Removed @RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    // Added explicit constructor
    public AuthController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}