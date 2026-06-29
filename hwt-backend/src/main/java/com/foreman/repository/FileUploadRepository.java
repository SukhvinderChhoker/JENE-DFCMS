package com.foreman.repository;

import com.foreman.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    List<FileUpload> findByCaseEntityIdAndDeletedFalse(Long caseId);
    List<FileUpload> findByTaskIdAndDeletedFalse(Long taskId);
    List<FileUpload> findByEvidenceIdAndDeletedFalse(Long evidenceId);
    List<FileUpload> findByUploaderId(Long userId);
}
