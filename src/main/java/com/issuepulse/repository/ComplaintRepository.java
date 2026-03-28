package com.issuepulse.repository;

import com.issuepulse.entity.Complaint;
import com.issuepulse.entity.Complaint.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Complaint> findByStatusOrderByUpdatedAtDesc(Status status);

    List<Complaint> findAllByOrderByCreatedAtDesc();

    List<Complaint> findByFlaggedAsFraudTrue();

    // ── Analytics queries ────────────────────────────────────────────────────

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category ORDER BY COUNT(c) DESC")
    List<Object[]> countByCategory();

    @Query("SELECT c.location, COUNT(c) FROM Complaint c GROUP BY c.location ORDER BY COUNT(c) DESC")
    List<Object[]> countByLocation();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countByStatus();

    /**
     * Duplicate detection: same title submitted by the same user more than once.
     * Uses COUNT(c) directly in HAVING — alias in HAVING is non-standard JPQL.
     */
    @Query("SELECT c.title, c.user.id, COUNT(c) FROM Complaint c GROUP BY c.title, c.user.id HAVING COUNT(c) > 1")
    List<Object[]> findDuplicateTitlesByUser();

    @Query("SELECT c.category, COUNT(c) FROM Complaint c WHERE c.status = :status GROUP BY c.category")
    List<Object[]> countByCategoryAndStatus(@Param("status") Status status);
}
