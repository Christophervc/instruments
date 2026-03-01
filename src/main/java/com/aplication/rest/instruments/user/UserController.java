package com.aplication.rest.instruments.user;

import com.aplication.rest.instruments.auth.dto.UserProfileDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.user.dto.ChangeRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Change user role", description = "Admin can change any user role")
    @PutMapping("/{id}/role")
    public ResponseEntity<Result<UserProfileDTO>> changeRole(@PathVariable UUID id, @Valid @RequestBody ChangeRoleRequest request){
        return ResponseEntity.ok(userService.changeRole(id, request));
    }
}
