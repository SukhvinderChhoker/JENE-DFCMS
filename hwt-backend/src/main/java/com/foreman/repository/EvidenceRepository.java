package com.foreman.repository;

import com.foreman.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByCaseEntityId(Long caseId);
    List<Evidence> findByUserId(Long userId);
    long countByCaseEntityId(Long caseId);
    long countByCurrentStatus(String status);
    Optional<Evidence> findByReference(String reference);
}
