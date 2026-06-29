package com.foreman.repository;

import com.foreman.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCaseEntityId(Long caseId);
    long countByCurrentStatus(String status);
    long countByCaseEntityId(Long caseId);
}
