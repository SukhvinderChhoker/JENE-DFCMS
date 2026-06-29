package com.foreman.repository;

import com.foreman.model.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseTypeRepository extends JpaRepository<CaseType, Long> {
    Optional<CaseType> findByCaseType(String caseType);
}
