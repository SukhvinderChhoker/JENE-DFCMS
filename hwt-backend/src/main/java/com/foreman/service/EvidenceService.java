package com.foreman.service;

import com.foreman.dto.ChainOfCustodyDTO;
import com.foreman.dto.EvidenceDTO;
import com.foreman.model.*;
import com.foreman.repository.*;
import com.foreman.util.InputSanitizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvidenceService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".txt",".doc",".docx",".xls",".xlsx",".ppt",".pptx",
        ".pdf",".csv",".rtf",".html",".xml",".zip",".gif",
        ".png",".jpg",".jpeg",".bmp",".tif",".tiff",".msg"
    );
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
        ".exe",".bat",".cmd",".com",".vbs",".js",".msi",
        ".dll",".scr",".pif",".jsp",".jspx",".war",".asp",".aspx"
    );

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EvidenceTypeRepository evidenceTypeRepository;

    @Autowired
    private ChainOfCustodyRepository chainOfCustodyRepository;

    @Autowired
    private ForemanOptionsRepository foremanOptionsRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private InputSanitizeUtil sanitizeUtil;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<EvidenceDTO> getAllEvidence(Long userId) {
        List<Evidence> evidenceList;
        if (userId != null) {
            evidenceList = evidenceRepository.findByUserId(userId);
        } else {
            evidenceList = evidenceRepository.findAll();
        }
        return evidenceList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public EvidenceDTO getEvidenceById(Long id) {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        return convertToDTO(evidence);
    }

    @Transactional
    public EvidenceDTO createEvidence(Long caseId, EvidenceDTO dto, Long userId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ForemanOptions options = foremanOptionsRepository.findFirstByOrderByIdAsc().orElse(new ForemanOptions());

        Evidence evidence = new Evidence();
        evidence.setCaseEntity(caseEntity);
        evidence.setUser(user);

        long count = evidenceRepository.count() + 1;
        evidence.setReference("EVD-" + String.format("%04d", count));

        evidence.setComment(sanitizeUtil.sanitize(dto.getComment()));
        evidence.setOriginator(sanitizeUtil.sanitize(dto.getOriginator()));
        evidence.setEvidenceBagNumber(sanitizeUtil.sanitize(dto.getEvidenceBagNumber()));
        evidence.setLocation(sanitizeUtil.sanitize(dto.getLocation()));
        evidence.setQrCode(dto.isQrCode());
        evidence.setQrCodeText(dto.getQrCodeText());
        evidence.setCurrentStatus(EvidenceStatus.INACTIVE);
        evidence.setDateAdded(LocalDateTime.now());

        if (dto.getType() != null) {
            evidenceTypeRepository.findByEvidenceType(dto.getType())
                    .ifPresent(evidence::setType);
        }

        if (options.isEvidenceRetention()) {
            evidence.setRetentionStartDate(LocalDateTime.now());
            evidence.setRetentionDate(LocalDateTime.now().plusDays(options.getEvidenceRetentionPeriod()));
        }

        EvidenceStatus status = new EvidenceStatus();
        status.setEvidence(evidence);
        status.setStatus(EvidenceStatus.INACTIVE);
        status.setDateTime(LocalDateTime.now());
        status.setUser(user);
        evidence.getStatuses().add(status);

        evidence = evidenceRepository.save(evidence);
        return convertToDTO(evidence);
    }

    @Transactional
    public EvidenceDTO updateEvidence(Long id, EvidenceDTO dto, Long userId) {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        evidence.setComment(sanitizeUtil.sanitize(dto.getComment()));
        evidence.setOriginator(sanitizeUtil.sanitize(dto.getOriginator()));
        evidence.setEvidenceBagNumber(sanitizeUtil.sanitize(dto.getEvidenceBagNumber()));
        evidence.setLocation(sanitizeUtil.sanitize(dto.getLocation()));
        evidence.setQrCode(dto.isQrCode());
        evidence.setQrCodeText(dto.getQrCodeText());

        if (dto.getType() != null) {
            evidenceTypeRepository.findByEvidenceType(dto.getType())
                    .ifPresent(evidence::setType);
        }

        EvidenceHistory hist = new EvidenceHistory();
        hist.setEvidence(evidence);
        hist.setUser(user);
        hist.setCaseEntity(evidence.getCaseEntity());
        hist.setDateTime(LocalDateTime.now());
        hist.setReference(evidence.getReference());
        hist.setType(evidence.getType());
        hist.setQrCode(evidence.isQrCode());
        hist.setQrCodeText(evidence.getQrCodeText());
        hist.setComment(evidence.getComment());
        hist.setOriginator(evidence.getOriginator());
        hist.setEvidenceBagNumber(evidence.getEvidenceBagNumber());
        hist.setLocation(evidence.getLocation());
        evidence.getHistory().add(hist);

        evidence = evidenceRepository.save(evidence);
        return convertToDTO(evidence);
    }

    @Transactional
    public EvidenceDTO changeStatus(Long id, String status, String note, Long userId) {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        evidence.setCurrentStatus(status);

        EvidenceStatus evidenceStatus = new EvidenceStatus();
        evidenceStatus.setEvidence(evidence);
        evidenceStatus.setStatus(status);
        evidenceStatus.setDateTime(LocalDateTime.now());
        evidenceStatus.setUser(user);
        evidenceStatus.setNote(note);
        evidence.getStatuses().add(evidenceStatus);

        evidence = evidenceRepository.save(evidence);
        return convertToDTO(evidence);
    }

    @Transactional
    public ChainOfCustodyDTO checkIn(Long evidenceId, String custodian, LocalDateTime date, String comment, Long userId) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChainOfCustody coc = new ChainOfCustody();
        coc.setEvidence(evidence);
        coc.setUser(user);
        coc.setCustodian(custodian);
        coc.setDateOfCustody(date);
        coc.setCheckIn(true);
        coc.setComment(comment);
        coc.setDateRecorded(LocalDateTime.now());

        coc = chainOfCustodyRepository.save(coc);
        return convertChainToDTO(coc);
    }

    @Transactional
    public ChainOfCustodyDTO checkOut(Long evidenceId, String custodian, LocalDateTime date, String comment, Long userId) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChainOfCustody coc = new ChainOfCustody();
        coc.setEvidence(evidence);
        coc.setUser(user);
        coc.setCustodian(custodian);
        coc.setDateOfCustody(date);
        coc.setCheckIn(false);
        coc.setComment(comment);
        coc.setDateRecorded(LocalDateTime.now());

        coc = chainOfCustodyRepository.save(coc);
        return convertChainToDTO(coc);
    }

    public List<ChainOfCustody> getChainOfCustody(Long evidenceId) {
        return chainOfCustodyRepository.findByEvidenceIdOrderByDateRecordedDesc(evidenceId);
    }

    public List<EvidenceHistory> getEvidenceHistory(Long id) {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        return evidence.getHistory();
    }

    public List<FileUpload> getEvidencePhotos(Long evidenceId) {
        evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        return fileUploadRepository.findByEvidenceIdAndDeletedFalse(evidenceId);
    }

    @Transactional
    public Map<String, Object> uploadDocument(Long evidenceId, MultipartFile file, String note, Long userId) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateFileExtension(file);

        try {
            String uploadDir = "uploads/evidence/docs/";
            Files.createDirectories(Paths.get(uploadDir));

            String ext = "";
            String name = file.getOriginalFilename();
            if (name != null && name.contains(".")) {
                ext = name.substring(name.lastIndexOf("."));
            }
            String fileName = "evidence_" + evidenceId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileTitle(file.getOriginalFilename());
            fileUpload.setFileName(fileName);
            fileUpload.setUploadLocation("/uploads/evidence/docs/" + fileName);
            fileUpload.setFileType(FileUpload.FileType.EVIDENCE);
            fileUpload.setEvidenceId(evidenceId);
            fileUpload.setCaseEntity(evidence.getCaseEntity());
            fileUpload.setUploader(user);
            fileUpload.setFileNote(note);
            fileUpload.setDateTime(LocalDateTime.now());
            fileUploadRepository.save(fileUpload);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", fileUpload.getId());
            result.put("fileName", fileUpload.getFileTitle());
            result.put("uploadLocation", fileUpload.getUploadLocation());
            result.put("note", fileUpload.getFileNote());
            result.put("dateTime", fileUpload.getDateTime().toString());
            result.put("uploaderName", user.getFullName());
            result.put("message", "Document uploaded successfully");
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getDocuments(Long evidenceId) {
        evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        List<FileUpload> files = fileUploadRepository.findByEvidenceIdAndDeletedFalse(evidenceId);
        return files.stream().map(f -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", f.getId());
            map.put("fileName", f.getFileTitle());
            map.put("uploadLocation", f.getUploadLocation());
            map.put("note", f.getFileNote());
            map.put("dateTime", f.getDateTime() != null ? f.getDateTime().toString() : null);
            map.put("uploaderName", f.getUploader() != null ? f.getUploader().getFullName() : "Unknown");
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public String uploadPhoto(Long evidenceId, MultipartFile file) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));

        validateFileExtension(file);

        try {
            String uploadDir = "uploads/evidence/";
            Files.createDirectories(Paths.get(uploadDir));

            String ext = "";
            String name = file.getOriginalFilename();
            if (name != null && name.contains(".")) {
                ext = name.substring(name.lastIndexOf("."));
            }
            String fileName = "evidence_" + evidenceId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            evidence.setPhotoUrl("/uploads/evidence/" + fileName);
            evidence.setPhotoFileName(file.getOriginalFilename());
            evidenceRepository.save(evidence);

            return "/uploads/evidence/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo: " + e.getMessage());
        }
    }

    public EvidenceDTO convertToDTO(Evidence evidence) {
        EvidenceDTO dto = new EvidenceDTO();
        dto.setId(evidence.getId());
        dto.setReference(evidence.getReference());
        dto.setCaseId(evidence.getCaseEntity().getId());
        dto.setCaseName(evidence.getCaseEntity().getCaseName());
        dto.setQrCode(evidence.isQrCode());
        dto.setComment(evidence.getComment());
        dto.setOriginator(evidence.getOriginator());
        dto.setEvidenceBagNumber(evidence.getEvidenceBagNumber());
        dto.setLocation(evidence.getLocation());
        dto.setCurrentStatus(evidence.getCurrentStatus());

        if (evidence.getType() != null) {
            dto.setType(evidence.getType().getEvidenceType());
        }

        if (evidence.getDateAdded() != null) {
            dto.setDateAdded(evidence.getDateAdded().format(FORMATTER));
        }

        dto.setPhotoUrl(evidence.getPhotoUrl());
        dto.setPhotoFileName(evidence.getPhotoFileName());

        return dto;
    }

    private ChainOfCustodyDTO convertChainToDTO(ChainOfCustody coc) {
        ChainOfCustodyDTO dto = new ChainOfCustodyDTO();
        dto.setId(coc.getId());
        dto.setEvidenceId(coc.getEvidence().getId());
        dto.setUserFullname(coc.getUser().getFullName());
        dto.setCustodian(coc.getCustodian());
        dto.setCheckIn(coc.isCheckIn());
        dto.setComment(coc.getComment());

        if (coc.getDateRecorded() != null) {
            dto.setDateRecorded(coc.getDateRecorded().format(FORMATTER));
        }
        if (coc.getDateOfCustody() != null) {
            dto.setDateOfCustody(coc.getDateOfCustody().format(FORMATTER));
        }

        return dto;
    }

    private void validateFileExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            if (BLOCKED_EXTENSIONS.contains(ext)) {
                throw new RuntimeException("File type not allowed: " + ext);
            }
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                throw new RuntimeException("File type not permitted: " + ext);
            }
        }
    }
}
