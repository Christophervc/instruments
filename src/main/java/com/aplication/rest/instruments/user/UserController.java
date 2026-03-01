package com.aplication.rest.instruments.user;

import com.aplication.rest.instruments.auth.dto.UserProfileDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management endpoints (only available for admin and staff)")
public class UserController {
    private final IUserService userService;

    @Operation(summary = "Get all users", description = "Admin can see all users, staff can see only customers")
    @GetMapping
    public ResponseEntity<Result<Page<UserProfileDTO>>> findAllUsers(
            @PageableDefault() Pageable pageable){
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @Operation(summary = "Get user by id", description = "Admin can see any user, staff can see only customers")
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserProfileDTO>> findById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.findById(id));
    }
}
