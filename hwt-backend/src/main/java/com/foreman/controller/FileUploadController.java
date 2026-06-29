package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.model.Case;
import com.foreman.model.FileUpload;
import com.foreman.model.User;
import com.foreman.repository.CaseRepository;
import com.foreman.repository.FileUploadRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/uploads")
public class FileUploadController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".pdf", ".doc", ".docx", ".txt", ".rtf",
        ".xls", ".xlsx", ".csv",
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".webp",
        ".zip", ".rar", ".7z",
        ".json", ".xml", ".log",
        ".pcap", ".dd", ".raw", ".img", ".E01", ".s01",
        ".ppt", ".pptx"
    );

    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
        ".exe", ".bat", ".cmd", ".com", ".msi", ".scr", ".pif",
        ".jsp", ".jspx", ".asp", ".aspx", ".php", ".py", ".rb", ".sh", ".bash",
        ".war", ".ear", ".jar", ".class",
        ".dll", ".so", ".dylib",
        ".vbs", ".vbe", ".js", ".wsf", ".wsc"
    );

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private RoleHelper roleHelper;

    private User requireAuth(HttpServletRequest request) {
        User user = roleHelper.getCurrentUser(request);
        if (user == null) throw new RuntimeException("Unauthorized");
        return user;
    }

    private String sanitizeFilename(String name) {
        if (name == null) return "unnamed";
        name = name.replaceAll("[^a-zA-Z0-9._\\-]", "_");
        name = name.replaceAll("\\.\\.", "_");
        if (name.length() > 200) name = name.substring(0, 200);
        return name;
    }

    private String validateAndExtractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        if (BLOCKED_EXTENSIONS.contains(ext)) {
            throw new RuntimeException("File type not allowed: " + ext);
        }
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new RuntimeException("File type not in allowed list: " + ext);
        }
        return ext;
    }

    @PostMapping("/case/{caseId}")
    public ResponseEntity<Map<String, Object>> uploadCaseDocument(
            @PathVariable Long caseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "type", defaultValue = "CASE") String type,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageCases(user)) {
            throw new RuntimeException("Access denied: Only Case Managers and Admins can upload case documents");
        }
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        String ext = validateAndExtractExtension(file.getOriginalFilename());

        try {
            String uploadDir = "uploads/cases/" + caseId + "/";
            Files.createDirectories(Paths.get(uploadDir));

            String safeName = sanitizeFilename(file.getOriginalFilename());
            String fileName = "case_" + caseId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileTitle(safeName);
            fileUpload.setFileName(fileName);
            fileUpload.setUploadLocation("/uploads/cases/" + caseId + "/" + fileName);
            fileUpload.setFileType(FileUpload.FileType.valueOf(type));
            fileUpload.setCaseEntity(caseEntity);
            fileUpload.setUploader(user);
            fileUpload.setFileNote(note);
            fileUpload.setDateTime(LocalDateTime.now());
            fileUploadRepository.save(fileUpload);

            Map<String, Object> result = new HashMap<>();
            result.put("id", fileUpload.getId());
            result.put("fileName", fileUpload.getFileTitle());
            result.put("uploadLocation", fileUpload.getUploadLocation());
            result.put("note", fileUpload.getFileNote());
            result.put("dateTime", fileUpload.getDateTime().toString());
            result.put("uploaderName", user.getFullName());
            result.put("message", "Document uploaded successfully");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document");
        }
    }

    @PostMapping("/task/{taskId}")
    public ResponseEntity<Map<String, Object>> uploadTaskDocument(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageTasks(user)) {
            throw new RuntimeException("Access denied: Only Case Managers and Admins can upload task documents");
        }

        String ext = validateAndExtractExtension(file.getOriginalFilename());

        try {
            String uploadDir = "uploads/tasks/" + taskId + "/";
            Files.createDirectories(Paths.get(uploadDir));

            String safeName = sanitizeFilename(file.getOriginalFilename());
            String fileName = "task_" + taskId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileTitle(safeName);
            fileUpload.setFileName(fileName);
            fileUpload.setUploadLocation("/uploads/tasks/" + taskId + "/" + fileName);
            fileUpload.setFileType(FileUpload.FileType.TASK);
            fileUpload.setTaskId(taskId);
            fileUpload.setUploader(user);
            fileUpload.setFileNote(note);
            fileUpload.setDateTime(LocalDateTime.now());
            fileUploadRepository.save(fileUpload);

            Map<String, Object> result = new HashMap<>();
            result.put("id", fileUpload.getId());
            result.put("fileName", fileUpload.getFileTitle());
            result.put("uploadLocation", fileUpload.getUploadLocation());
            result.put("note", fileUpload.getFileNote());
            result.put("dateTime", fileUpload.getDateTime().toString());
            result.put("uploaderName", user.getFullName());
            result.put("message", "Document uploaded successfully");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document");
        }
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Map<String, Object>>> getCaseDocuments(
            @PathVariable Long caseId,
            HttpServletRequest request) {
        requireAuth(request);
        List<FileUpload> files = fileUploadRepository.findByCaseEntityIdAndDeletedFalse(caseId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (FileUpload f : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("fileName", f.getFileTitle());
            map.put("uploadLocation", f.getUploadLocation());
            map.put("note", f.getFileNote());
            map.put("dateTime", f.getDateTime() != null ? f.getDateTime().toString() : null);
            map.put("uploaderName", f.getUploader() != null ? f.getUploader().getFullName() : "Unknown");
            map.put("fileType", f.getFileType() != null ? f.getFileType().name() : "CASE");
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Map<String, Object>>> getTaskDocuments(
            @PathVariable Long taskId,
            HttpServletRequest request) {
        requireAuth(request);
        List<FileUpload> files = fileUploadRepository.findByTaskIdAndDeletedFalse(taskId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (FileUpload f : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("fileName", f.getFileTitle());
            map.put("uploadLocation", f.getUploadLocation());
            map.put("note", f.getFileNote());
            map.put("dateTime", f.getDateTime() != null ? f.getDateTime().toString() : null);
            map.put("uploaderName", f.getUploader() != null ? f.getUploader().getFullName() : "Unknown");
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }
}
