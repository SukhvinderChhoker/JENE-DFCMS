package com.foreman.repository;

import com.foreman.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findByCurrentStatus(String status);
    Optional<Case> findByReference(String reference);

    @Query("SELECT c FROM Case c WHERE c.id IN " +
           "(SELECT ucr.caseEntity.id FROM UserCaseRole ucr WHERE ucr.user.id = :userId)")
    List<Case> findCasesByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Case c WHERE c.currentStatus = :status AND c.id IN " +
           "(SELECT ucr.caseEntity.id FROM UserCaseRole ucr WHERE ucr.user.id = :userId)")
    List<Case> findCasesByStatusAndUserId(@Param("status") String status, @Param("userId") Long userId);

    long countByCurrentStatus(String status);
}
