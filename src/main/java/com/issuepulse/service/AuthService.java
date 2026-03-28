package com.issuepulse.service;

import com.issuepulse.dto.Dtos.*;
import com.issuepulse.entity.User;
import com.issuepulse.repository.UserRepository;
import com.issuepulse.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils jwtUtils;

    @Value("${app.student.email-domain}")
    private String studentEmailDomain;

    // ── Register ──────────────────────────────────────────────────────────────

    public AuthResponse register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email is already registered.");
        }

        validateEmailDomain(req.getEmail(), req.getRole());

        User user = User.builder()
            .name(req.getName())
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .role(req.getRole())
            // ── save rollNo & department if provided ──
            .rollNo(req.getRollNo())
            .department(req.getDepartment())
            .build();

        User saved = userRepository.save(user);

        String token = jwtUtils.generateTokenFromEmail(saved.getEmail());

        return new AuthResponse(saved.getId(), saved.getName(), saved.getEmail(),
            saved.getRole().name(), token, "Registration successful! Welcome to IssuePulse.");
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new RuntimeException("No account found with this email."));

        validateEmailDomain(req.getEmail(), user.getRole());

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Incorrect password.");
        }

        String token = jwtUtils.generateToken(auth);

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(),
            user.getRole().name(), token, "Login successful! Welcome back, " + user.getName() + ".");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateEmailDomain(String email, User.Role role) {
        boolean isStudentDomain = email.toLowerCase().endsWith(studentEmailDomain.toLowerCase());

        if (role == User.Role.STUDENT && !isStudentDomain) {
            throw new RuntimeException("Student email must end with " + studentEmailDomain);
        }
        if (role == User.Role.ADMIN && isStudentDomain) {
            throw new RuntimeException("Admin email must NOT end with " + studentEmailDomain);
        }
    }
}
