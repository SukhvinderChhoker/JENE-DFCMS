package com.foreman.repository;

import com.foreman.model.CasePriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CasePriorityRepository extends JpaRepository<CasePriority, Long> {
    Optional<CasePriority> findByCasePriority(String casePriority);
    Optional<CasePriority> findByIsDefaultTrue();
}
