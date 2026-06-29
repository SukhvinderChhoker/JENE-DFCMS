package com.foreman.repository;

import com.foreman.model.TaskTimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTimeSheetRepository extends JpaRepository<TaskTimeSheet, Long> {
    List<TaskTimeSheet> findByTaskId(Long taskId);
    List<TaskTimeSheet> findByUserId(Long userId);
}
