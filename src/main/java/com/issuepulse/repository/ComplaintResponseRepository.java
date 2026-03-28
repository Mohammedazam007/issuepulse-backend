package com.issuepulse.repository;

import com.issuepulse.entity.ComplaintResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintResponseRepository extends JpaRepository<ComplaintResponse, Long> {
    Optional<ComplaintResponse> findByComplaintId(Long complaintId);
}
