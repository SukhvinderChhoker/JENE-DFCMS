package com.foreman.service;

import com.foreman.dto.CaseDTO;
import com.foreman.model.*;
import com.foreman.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private UserCaseRoleRepository userCaseRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaseClassificationRepository caseClassificationRepository;

    @Autowired
    private CaseTypeRepository caseTypeRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private CasePriorityRepository casePriorityRepository;

    @Autowired
    private ForemanOptionsRepository foremanOptionsRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<CaseDTO> getAllCases(String status, Long userId) {
        List<Case> cases;
        if (status != null && userId != null) {
            cases = caseRepository.findCasesByStatusAndUserId(status, userId);
        } else if (userId != null) {
            cases = caseRepository.findCasesByUserId(userId);
        } else if (status != null) {
            cases = caseRepository.findByCurrentStatus(status);
        } else {
            cases = caseRepository.findAll();
        }
        return cases.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CaseDTO getCaseById(Long id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return convertToDTO(caseEntity);
    }

    @Transactional
    public CaseDTO createCase(CaseDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ForemanOptions options = foremanOptionsRepository.findFirstByOrderByIdAsc().orElse(new ForemanOptions());

        Case caseEntity = new Case();
        caseEntity.setCaseName(dto.getCaseName());

        long count = caseRepository.count() + 1;
        String prefix = options.getCaseNames() != null ? options.getCaseNames() : "CASE";
        caseEntity.setReference(prefix + "-" + String.format("%04d", count));

        caseEntity.setBackground(dto.getBackground());
        caseEntity.setPrivateCase(dto.isPrivateCase());
        caseEntity.setLocation(dto.getLocation());
        caseEntity.setJustification(dto.getJustification());
        caseEntity.setCreationDate(LocalDateTime.now());

        if (dto.getClassification() != null) {
            caseClassificationRepository.findByClassification(dto.getClassification())
                    .ifPresent(caseEntity::setClassification);
        }

        if (dto.getCaseType() != null) {
            caseTypeRepository.findByCaseType(dto.getCaseType())
                    .ifPresent(caseEntity::setCaseType);
        }

        if (dto.getCasePriority() != null) {
            var priority = casePriorityRepository.findByCasePriority(dto.getCasePriority()).orElse(null);
            if (priority != null) {
                caseEntity.setCasePriority(priority);
                caseEntity.setCasePriorityColour(priority.getColour());
            }
        } else {
            var priority = casePriorityRepository.findByIsDefaultTrue().orElse(null);
            if (priority != null) {
                caseEntity.setCasePriority(priority);
                caseEntity.setCasePriorityColour(priority.getColour());
            }
        }

        if (dto.getDeadline() != null) {
            try {
                caseEntity.setDeadline(LocalDateTime.parse(dto.getDeadline(), FORMATTER));
            } catch (Exception e) {
                try {
                    caseEntity.setDeadline(LocalDateTime.parse(dto.getDeadline(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception ex) {
                    // ignore invalid date
                }
            }
        }

        if (dto.getDocumentChecklist() != null) {
            var cl = dto.getDocumentChecklist();
            caseEntity.setCocReceived(Boolean.TRUE.equals(cl.get("cocReceived")));
            caseEntity.setAgencyLetterReceived(Boolean.TRUE.equals(cl.get("agencyLetterReceived")));
            caseEntity.setAuthorityLetterReceived(Boolean.TRUE.equals(cl.get("authorityLetterReceived")));
            caseEntity.setConsentFormReceived(Boolean.TRUE.equals(cl.get("consentFormReceived")));
            caseEntity.setCaseHistoryReceived(Boolean.TRUE.equals(cl.get("caseHistoryReceived")));
            caseEntity.setHandlingTakingFormReceived(Boolean.TRUE.equals(cl.get("handlingTakingFormReceived")));
            caseEntity.setEvidencePhotosReceived(Boolean.TRUE.equals(cl.get("evidencePhotosReceived")));
            caseEntity.setSeizureMemoReceived(Boolean.TRUE.equals(cl.get("seizureMemoReceived")));
            caseEntity.setWitnessStatementReceived(Boolean.TRUE.equals(cl.get("witnessStatementReceived")));
            caseEntity.setOtherDocumentsReceived(Boolean.TRUE.equals(cl.get("otherDocumentsReceived")));
        }

        caseEntity.setStatus(CaseStatus.PENDING, user, "Case created");

        caseEntity = caseRepository.save(caseEntity);

        UserCaseRole requesterRole = new UserCaseRole(user, caseEntity, UserCaseRole.REQUESTER);
        userCaseRoleRepository.save(requesterRole);

        return convertToDTO(caseEntity);
    }

    @Transactional
    public CaseDTO updateCase(Long id, CaseDTO dto, Long userId) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        caseEntity.setCaseName(dto.getCaseName());
        caseEntity.setBackground(dto.getBackground());
        caseEntity.setPrivateCase(dto.isPrivateCase());
        caseEntity.setLocation(dto.getLocation());
        caseEntity.setJustification(dto.getJustification());

        if (dto.getClassification() != null) {
            caseClassificationRepository.findByClassification(dto.getClassification())
                    .ifPresent(caseEntity::setClassification);
        }

        if (dto.getCaseType() != null) {
            caseTypeRepository.findByCaseType(dto.getCaseType())
                    .ifPresent(caseEntity::setCaseType);
        }

        if (dto.getCasePriority() != null) {
            var priority = casePriorityRepository.findByCasePriority(dto.getCasePriority()).orElse(null);
            if (priority != null) {
                caseEntity.setCasePriority(priority);
                caseEntity.setCasePriorityColour(priority.getColour());
            }
        }

        if (dto.getDeadline() != null) {
            try {
                caseEntity.setDeadline(LocalDateTime.parse(dto.getDeadline(), FORMATTER));
            } catch (Exception e) {
                try {
                    caseEntity.setDeadline(LocalDateTime.parse(dto.getDeadline(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception ex) {
                    // ignore invalid date
                }
            }
        }

        caseEntity.addChange(user);
        caseEntity = caseRepository.save(caseEntity);

        return convertToDTO(caseEntity);
    }

    @Transactional
    public CaseDTO changeStatus(Long id, String status, String reason, Long userId) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        caseEntity.setStatus(status, user, reason);
        caseEntity.addChange(user);
        caseEntity = caseRepository.save(caseEntity);

        return convertToDTO(caseEntity);
    }

    @Transactional
    public CaseDTO closeCase(Long id, String reason, Long userId) {
        return changeStatus(id, CaseStatus.CLOSED, reason, userId);
    }

    @Transactional
    public CaseDTO authorizeCase(Long id, boolean authorize, String reason, Long userId) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (authorize) {
            CaseStatus cs = new CaseStatus();
            cs.setCaseEntity(caseEntity);
            cs.setStatus(CaseStatus.OPEN);
            cs.setDateTime(LocalDateTime.now());
            cs.setUser(user);
            cs.setReason(reason != null ? reason : "Case authorized");
            caseEntity.getStatuses().add(cs);
            caseEntity.setCurrentStatus(CaseStatus.OPEN);
        } else {
            CaseStatus cs = new CaseStatus();
            cs.setCaseEntity(caseEntity);
            cs.setStatus(CaseStatus.REJECTED);
            cs.setDateTime(LocalDateTime.now());
            cs.setUser(user);
            cs.setReason(reason != null ? reason : "Case rejected");
            caseEntity.getStatuses().add(cs);
            caseEntity.setCurrentStatus(CaseStatus.REJECTED);
        }

        caseEntity.addChange(user);
        caseEntity = caseRepository.save(caseEntity);

        return convertToDTO(caseEntity);
    }

    public List<CaseHistory> getCaseHistory(Long id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return caseEntity.getHistory();
    }

    public List<com.foreman.model.Task> getCaseTasks(Long caseId) {
        return taskRepository.findByCaseEntityId(caseId);
    }

    public List<Evidence> getCaseEvidence(Long caseId) {
        return evidenceRepository.findByCaseEntityId(caseId);
    }

    public List<FileUpload> getCaseUploads(Long caseId) {
        caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return fileUploadRepository.findByCaseEntityIdAndDeletedFalse(caseId);
    }

    @Transactional
    public void linkCases(Long caseId, Long linkedCaseId, String reason, Long userId) {
        // Store link reference in case history
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        caseEntity.addChange(user);
        caseRepository.save(caseEntity);
    }

    @Transactional
    public void unlinkCases(Long caseId, Long linkedCaseId, String reason, Long userId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        caseEntity.addChange(user);
        caseRepository.save(caseEntity);
    }

    public List<Case> getLinkedCases(Long caseId) {
        return new ArrayList<>();
    }

    public CaseDTO convertToDTO(Case caseEntity) {
        CaseDTO dto = new CaseDTO();
        dto.setId(caseEntity.getId());
        dto.setCaseName(caseEntity.getCaseName());
        dto.setReference(caseEntity.getReference());
        dto.setCurrentStatus(caseEntity.getCurrentStatus());
        dto.setPrivateCase(caseEntity.isPrivateCase());
        dto.setBackground(caseEntity.getBackground());
        dto.setLocation(caseEntity.getLocation());

        if (caseEntity.getCreationDate() != null) {
            dto.setCreationDate(caseEntity.getCreationDate().format(FORMATTER));
        }

        if (caseEntity.getClassification() != null) {
            dto.setClassification(caseEntity.getClassification().getClassification());
        }

        if (caseEntity.getCaseType() != null) {
            dto.setCaseType(caseEntity.getCaseType().getCaseType());
        }

        dto.setJustification(caseEntity.getJustification());

        if (caseEntity.getCasePriority() != null) {
            dto.setCasePriority(caseEntity.getCasePriority().getCasePriority());
        }

        dto.setCasePriorityColour(caseEntity.getCasePriorityColour());

        if (caseEntity.getDeadline() != null) {
            dto.setDeadline(caseEntity.getDeadline().format(FORMATTER));
        }

        dto.setTaskCount((int) taskRepository.countByCaseEntityId(caseEntity.getId()));
        dto.setEvidenceCount((int) evidenceRepository.countByCaseEntityId(caseEntity.getId()));

        List<UserCaseRole> caseRoles = userCaseRoleRepository.findByCaseEntityId(caseEntity.getId());
        for (UserCaseRole ucr : caseRoles) {
            String fullname = ucr.getUser().getFullName();
            switch (ucr.getRole()) {
                case UserCaseRole.PRINCIPLE_CASE_MANAGER -> dto.setPrincipleCaseManager(fullname);
                case UserCaseRole.SECONDARY_CASE_MANAGER -> dto.setSecondaryCaseManager(fullname);
                case UserCaseRole.REQUESTER -> dto.setRequester(fullname);
            }
        }

        Map<String, Boolean> checklist = new HashMap<>();
        checklist.put("cocReceived", caseEntity.isCocReceived());
        checklist.put("agencyLetterReceived", caseEntity.isAgencyLetterReceived());
        checklist.put("authorityLetterReceived", caseEntity.isAuthorityLetterReceived());
        checklist.put("consentFormReceived", caseEntity.isConsentFormReceived());
        checklist.put("caseHistoryReceived", caseEntity.isCaseHistoryReceived());
        checklist.put("handlingTakingFormReceived", caseEntity.isHandlingTakingFormReceived());
        checklist.put("evidencePhotosReceived", caseEntity.isEvidencePhotosReceived());
        checklist.put("seizureMemoReceived", caseEntity.isSeizureMemoReceived());
        checklist.put("witnessStatementReceived", caseEntity.isWitnessStatementReceived());
        checklist.put("otherDocumentsReceived", caseEntity.isOtherDocumentsReceived());
        dto.setDocumentChecklist(checklist);

        return dto;
    }
}
