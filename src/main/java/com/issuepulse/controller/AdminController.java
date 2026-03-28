package com.issuepulse.controller;

import com.issuepulse.dto.Dtos.*;
import com.issuepulse.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * AdminController — Admin-only endpoints (requires ROLE_ADMIN).
 *
 * PUT  /api/admin/update-status/{id}   → Change complaint status
 * POST /api/admin/upload-resolution    → Add resolution message + image
 * POST /api/admin/respond              → Send message to student
 * GET  /api/admin/analytics            → Dashboard analytics summary
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private ComplaintService complaintService;

    // ── PUT /api/admin/update-status/{id} ─────────────────────────────────────
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody StatusUpdateRequest req) {
        try {
            return ResponseEntity.ok(complaintService.updateStatus(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/admin/upload-resolution ─────────────────────────────────────
    @PostMapping(value = "/upload-resolution", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResolution(
        @RequestParam("complaintId") Long complaintId,
        @RequestParam(value = "message", required = false) String message,
        @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            return ResponseEntity.ok(complaintService.uploadResolution(complaintId, message, image));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/admin/respond ────────────────────────────────────────────────
    // Lightweight: just send a text message without changing status
    @PostMapping("/respond")
    public ResponseEntity<?> respond(@RequestBody AdminResponseRequest req) {
        try {
            return ResponseEntity.ok(
                complaintService.uploadResolution(req.getComplaintId(), req.getAdminMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/admin/analytics ───────────────────────────────────────────────
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        try {
            return ResponseEntity.ok(complaintService.getAnalyticsSummary());
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 NOW ERROR WILL SHOW
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
