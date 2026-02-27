package com.aplication.rest.instruments.auth;


import com.aplication.rest.instruments.auth.dto.AuthResponse;
import com.aplication.rest.instruments.auth.dto.LoginRequest;
import com.aplication.rest.instruments.auth.dto.RegisterRequest;
import com.aplication.rest.instruments.core.error_handling.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Register and login endpoints")
public class AuthController {
    private final IAuthService authService;
    @Operation(summary = "Register a new user", description = "Creates a new account with customer role assigned by default")
    @SecurityRequirements()
    @PostMapping("/register")
    public ResponseEntity<Result<AuthResponse>> register(@RequestBody @Valid RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
    @Operation(summary = "Login a user", description = "Login a user and return a JWT token")
    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<Result<AuthResponse>> login(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
