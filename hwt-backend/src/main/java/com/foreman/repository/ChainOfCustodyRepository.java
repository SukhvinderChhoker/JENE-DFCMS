package com.foreman.repository;

import com.foreman.model.ChainOfCustody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChainOfCustodyRepository extends JpaRepository<ChainOfCustody, Long> {
    List<ChainOfCustody> findByEvidenceIdOrderByDateRecordedDesc(Long evidenceId);
}
