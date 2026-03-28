package com.issuepulse.controller;

import com.issuepulse.dto.Dtos.*;
import com.issuepulse.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * ComplaintController — Student-facing and public complaint endpoints.
 *
 * POST   /api/complaints/create          → Submit complaint (student)
 * GET    /api/complaints/my             → My complaints (student)
 * GET    /api/complaints/resolved       → All resolved complaints (public)
 * GET    /api/complaints/trending       → Trending issues (public)
 * GET    /api/complaints/all            → All complaints (admin)
 * GET    /api/complaints/{id}           → Single complaint
 */
@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired private ComplaintService complaintService;

    // ── POST /api/complaints/create ────────────────────────────────────────────
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createComplaint(
        @ModelAttribute ComplaintRequest req,
        @RequestParam(value = "image", required = false) MultipartFile image,
        Authentication auth) {
        try {
            ComplaintResponse resp = complaintService.createComplaint(req, image, auth.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/complaints/my ─────────────────────────────────────────────────
    @GetMapping("/my")
    public ResponseEntity<List<ComplaintResponse>> getMyComplaints(Authentication auth) {
        return ResponseEntity.ok(complaintService.getMyComplaints(auth.getName()));
    }

    // ── GET /api/complaints/resolved (public) ──────────────────────────────────
    @GetMapping("/resolved")
    public ResponseEntity<List<ComplaintResponse>> getResolvedComplaints() {
        return ResponseEntity.ok(complaintService.getResolvedComplaints());
    }

    // ── GET /api/complaints/trending (public) ──────────────────────────────────
    @GetMapping("/trending")
    public ResponseEntity<List<TrendingItem>> getTrending() {
        return ResponseEntity.ok(complaintService.getTrendingIssues());
    }

    // ── GET /api/complaints/all (admin) ────────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    // ── GET /api/complaints/{id} ───────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(complaintService.getComplaintById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
