package com.foreman.repository;

import com.foreman.model.CaseClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseClassificationRepository extends JpaRepository<CaseClassification, Long> {
    Optional<CaseClassification> findByClassification(String classification);
}
