package com.issuepulse.service;

import com.issuepulse.dto.Dtos.*;
import com.issuepulse.entity.User;
import com.issuepulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // ── Get Profile ───────────────────────────────────────────────────────────

    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found."));

        ProfileResponse resp = new ProfileResponse();
        resp.setUserId(user.getId());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole().name());
        resp.setRollNo(user.getRollNo());
        resp.setDepartment(user.getDepartment());
        return resp;
    }

    // ── Change Password ───────────────────────────────────────────────────────

    public ApiResponse changePassword(String email, ChangePasswordRequest req) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        return new ApiResponse(true, "Password changed successfully.");
    }
}
