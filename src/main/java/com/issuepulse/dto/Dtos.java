package com.issuepulse.dto;

import com.issuepulse.entity.Complaint.Status;
import com.issuepulse.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

public class Dtos {

    // =========================================================
    // AUTH
    // =========================================================

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String name;

        @Email @NotBlank
        private String email;

        @NotBlank @Size(min = 6)
        private String password;

        @NotNull
        private User.Role role;

        // Optional — only relevant for students
        private String rollNo;
        private String department;
    }

    @Data
    public static class LoginRequest {
        @Email @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private Long userId;
        private String name;
        private String email;
        private String role;
        private String token;
        private String message;

        public AuthResponse(Long userId, String name, String email,
                            String role, String token, String message) {
            this.userId = userId; this.name = name; this.email = email;
            this.role = role; this.token = token; this.message = message;
        }
    }

    // =========================================================
    // USER / PROFILE  ← NEW
    // =========================================================

    @Data
    public static class ProfileResponse {
        private Long userId;
        private String name;
        private String email;
        private String role;
        private String rollNo;
        private String department;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String currentPassword;

        @NotBlank @Size(min = 6)
        private String newPassword;
    }

    // =========================================================
    // COMPLAINTS
    // =========================================================

    @Data
    public static class ComplaintRequest {
        private String title;
        private String description;
        private String category;
        private String location;
    }

    @Data
    public static class ComplaintResponse {
        private Long id;
        private String title;
        private String description;
        private String category;
        private String location;
        private String imageUrl;
        private String status;
        private String createdAt;
        private String updatedAt;
        private boolean flaggedAsFraud;
        private String fraudReason;
        private Long userId;
        private String userName;
        private String userEmail;
        private String adminMessage;
        private String resolutionImageUrl;
        private String resolvedAt;
    }

    @Data
    public static class StatusUpdateRequest {
        @NotNull private Status status;
    }

    @Data
    public static class AdminResponseRequest {
        @NotNull private Long complaintId;
        private String adminMessage;
    }

    // =========================================================
    // ANALYTICS
    // =========================================================

    @Data
    public static class TrendingItem {
        private String label;
        private Long count;
        public TrendingItem(String label, Long count) {
            this.label = label; this.count = count;
        }
    }

    @Data
    public static class AnalyticsSummary {
        private Long totalComplaints;
        private Long pendingCount;
        private Long underProcessCount;
        private Long resolvedCount;
        private Long rejectedCount;
        private Long fraudCount;
        private List<TrendingItem> categoryBreakdown;
        private List<TrendingItem> locationBreakdown;
    }

    // =========================================================
    // GENERIC
    // =========================================================

    @Data
    public static class ApiResponse {
        private boolean success;
        private String message;
        public ApiResponse(boolean success, String message) {
            this.success = success; this.message = message;
        }
    }
}
