package com.issuepulse.controller;

import com.issuepulse.dto.Dtos.*;
import com.issuepulse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * UserController
 * GET  /api/user/profile          → returns logged-in user's profile
 * PUT  /api/user/change-password  → changes password after verifying current one
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired private UserService userService;

    // ── GET /api/user/profile ─────────────────────────────────────────────────
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();  // email from JWT
            return ResponseEntity.ok(userService.getProfile(email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/user/change-password ─────────────────────────────────────────
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest req) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(userService.changePassword(email, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
