package com.foreman.repository;

import com.foreman.model.EvidenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvidenceTypeRepository extends JpaRepository<EvidenceType, Long> {
    Optional<EvidenceType> findByEvidenceType(String evidenceType);
}
