package com.issuepulse.service;

import com.issuepulse.dto.Dtos.ComplaintRequest;
import com.issuepulse.dto.Dtos.ComplaintResponse;
import com.issuepulse.dto.Dtos.StatusUpdateRequest;
import com.issuepulse.dto.Dtos.AdminResponseRequest;
import com.issuepulse.dto.Dtos.TrendingItem;
import com.issuepulse.dto.Dtos.AnalyticsSummary;
import com.issuepulse.entity.Complaint;
import com.issuepulse.entity.User;
import com.issuepulse.entity.Complaint.Status;
import com.issuepulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    @Autowired private ComplaintRepository complaintRepo;
    @Autowired private ComplaintResponseRepository responseRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private FraudDetectionService fraudService;
    @Autowired private FileStorageService fileStorage;

    // ── Create complaint ───────────────────────────────────────────────────────

    public ComplaintResponse createComplaint(ComplaintRequest req, MultipartFile image, String userEmail) {
        User student = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Null-safe fraud detection (all fields are optional)
        FraudDetectionService.FraudResult fraud =
            fraudService.analyse(req.getTitle(), req.getDescription());

        // Build complaint — all user-supplied fields are optional
        Complaint complaint = Complaint.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .category(req.getCategory() != null ? req.getCategory() : "OTHER")
            .location(req.getLocation() != null ? req.getLocation() : "GENERAL")
            .status(Status.PENDING)
            .user(student)
            .flaggedAsFraud(fraud.flagged())
            .fraudReason(fraud.reason())
            .build();

        // Save image only if provided and non-empty
        if (image != null && !image.isEmpty()) {
            try {
                String url = fileStorage.storeFile(image, "complaints");
                complaint.setImageUrl(url);
            } catch (Exception e) {
                // Image failure must never block complaint creation
                System.err.println("Warning: image upload failed — " + e.getMessage());
            }
        }

        return toResponse(complaintRepo.save(complaint));
    }

    // ── Student: my complaints ─────────────────────────────────────────────────

    public List<ComplaintResponse> getMyComplaints(String userEmail) {
        User student = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return complaintRepo.findByUserIdOrderByCreatedAtDesc(student.getId())
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Public: resolved complaints ────────────────────────────────────────────

    public List<ComplaintResponse> getResolvedComplaints() {
        return complaintRepo.findByStatusOrderByUpdatedAtDesc(Status.RESOLVED)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Public: trending issues ────────────────────────────────────────────────

    public List<TrendingItem> getTrendingIssues() {
        return complaintRepo.countByCategory().stream()
        		.map(row -> new TrendingItem(
        			    row[0] != null ? row[0].toString() : "UNKNOWN",
        			    row[1] != null ? (Long) row[1] : 0L
        			))
            .collect(Collectors.toList());
    }

    // ── Admin: all complaints ──────────────────────────────────────────────────

    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepo.findAllByOrderByCreatedAtDesc()
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Admin: get single complaint ────────────────────────────────────────────

    public ComplaintResponse getComplaintById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── Admin: update status ───────────────────────────────────────────────────

    public ComplaintResponse updateStatus(Long id, StatusUpdateRequest req) {
        Complaint c = findOrThrow(id);
        c.setStatus(req.getStatus());
        return toResponse(complaintRepo.save(c));
    }

    // ── Admin: upload resolution ───────────────────────────────────────────────

    public ComplaintResponse uploadResolution(Long complaintId, String message, MultipartFile image) {
        Complaint c = findOrThrow(complaintId);

        // Find or create the response record
        com.issuepulse.entity.ComplaintResponse res = responseRepo
            .findByComplaintId(complaintId)
            .orElse(com.issuepulse.entity.ComplaintResponse.builder().complaint(c).build());

        res.setAdminMessage(message);

        if (image != null && !image.isEmpty()) {
            String url = fileStorage.storeFile(image, "resolutions");
            res.setResolutionImageUrl(url);
        }

        responseRepo.save(res);

        // Auto-set status to RESOLVED when resolution is uploaded
        
        return toResponse(complaintRepo.save(c));
    }

    // ── Admin: analytics summary ───────────────────────────────────────────────

    public AnalyticsSummary getAnalyticsSummary() {
        AnalyticsSummary s = new AnalyticsSummary();
        s.setTotalComplaints(complaintRepo.count());

        // Count by status
        for (Object[] row : complaintRepo.countByStatus()) {
            Status st = Status.valueOf(row[0].toString());
            Long cnt = (Long) row[1];
            switch (st) {
                case PENDING      -> s.setPendingCount(cnt);
                case UNDER_PROCESS -> s.setUnderProcessCount(cnt);
                case RESOLVED     -> s.setResolvedCount(cnt);
                case REJECTED     -> s.setRejectedCount(cnt);
            }
        }
        if (s.getPendingCount()      == null) s.setPendingCount(0L);
        if (s.getUnderProcessCount() == null) s.setUnderProcessCount(0L);
        if (s.getResolvedCount()     == null) s.setResolvedCount(0L);
        if (s.getRejectedCount()     == null) s.setRejectedCount(0L);

        s.setFraudCount(complaintRepo.findByFlaggedAsFraudTrue().size() * 1L);

        // Category + location breakdowns
        s.setCategoryBreakdown(complaintRepo.countByCategory().stream()
        		.map(r -> new TrendingItem(
        			    r[0] != null ? r[0].toString() : "UNKNOWN",
        			    r[1] != null ? (Long) r[1] : 0L
        			))
            .collect(Collectors.toList()));
        s.setLocationBreakdown(complaintRepo.countByLocation().stream()
        		.map(r -> new TrendingItem(
        			    r[0] != null ? r[0].toString() : "UNKNOWN",
        			    r[1] != null ? (Long) r[1] : 0L
        			))
            .collect(Collectors.toList()));

        return s;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Complaint findOrThrow(Long id) {
        return complaintRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found: " + id));
    }

    public ComplaintResponse toResponse(Complaint c) {
        ComplaintResponse r = new ComplaintResponse();
        r.setId(c.getId());
        r.setTitle(c.getTitle());
        r.setDescription(c.getDescription());
        r.setCategory(c.getCategory());   // plain String — no .name() needed
        r.setLocation(c.getLocation());
        r.setImageUrl(c.getImageUrl());
        r.setStatus(c.getStatus().name());
        r.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        r.setUpdatedAt(c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : null);
        r.setFlaggedAsFraud(c.isFlaggedAsFraud());
        r.setFraudReason(c.getFraudReason());

        if (c.getUser() != null) {
            r.setUserId(c.getUser().getId());
            r.setUserName(c.getUser().getName());
            r.setUserEmail(c.getUser().getEmail());
        }

        if (c.getResponse() != null) {
            r.setAdminMessage(c.getResponse().getAdminMessage());
            r.setResolutionImageUrl(c.getResponse().getResolutionImageUrl());
            r.setResolvedAt(c.getResponse().getUpdatedAt() != null
                ? c.getResponse().getUpdatedAt().toString() : null);
        }

        return r;
    }
}
