package com.issuepulse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ComplaintResponse — Stores admin's resolution message and image.
 * Created when admin resolves or responds to a complaint.
 */
@Entity
@Table(name = "complaint_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String adminMessage;

    @Column
    private String resolutionImageUrl;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Complaint complaint;
}
