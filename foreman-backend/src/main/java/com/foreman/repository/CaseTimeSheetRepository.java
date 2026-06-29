package com.foreman.repository;

import com.foreman.model.CaseTimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseTimeSheetRepository extends JpaRepository<CaseTimeSheet, Long> {
    List<CaseTimeSheet> findByCaseEntityId(Long caseId);
    List<CaseTimeSheet> findByUserId(Long userId);
}
