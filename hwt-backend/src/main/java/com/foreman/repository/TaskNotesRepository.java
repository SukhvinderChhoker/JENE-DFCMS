package com.foreman.repository;

import com.foreman.model.TaskNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskNotesRepository extends JpaRepository<TaskNotes, Long> {
    List<TaskNotes> findByTaskIdOrderByDateTimeDesc(Long taskId);
}
