package com.foreman.service;

import com.foreman.dto.DashboardDTO;
import com.foreman.model.*;
import com.foreman.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardDTO getDashboardStats() {
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setTotalCases(caseRepository.count());
        dashboard.setOpenCases(caseRepository.countByCurrentStatus(CaseStatus.OPEN));
        dashboard.setClosedCases(caseRepository.countByCurrentStatus(CaseStatus.CLOSED));
        dashboard.setTotalTasks(taskRepository.count());
        dashboard.setQueuedTasks(taskRepository.countByCurrentStatus(TaskStatus.QUEUED));
        dashboard.setCompletedTasks(taskRepository.countByCurrentStatus(TaskStatus.COMPLETE));
        dashboard.setTotalEvidence(evidenceRepository.count());
        dashboard.setActiveUsers(userRepository.findByActiveTrue().size());
        return dashboard;
    }

    public Map<String, Object> generateCaseReport(Long caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("caseId", caseEntity.getId());
        report.put("caseName", caseEntity.getCaseName());
        report.put("reference", caseEntity.getReference());
        report.put("status", caseEntity.getCurrentStatus());
        report.put("background", caseEntity.getBackground());
        report.put("location", caseEntity.getLocation());
        report.put("creationDate", caseEntity.getCreationDate());
        report.put("deadline", caseEntity.getDeadline());

        if (caseEntity.getClassification() != null) {
            report.put("classification", caseEntity.getClassification().getClassification());
        }
        if (caseEntity.getCaseType() != null) {
            report.put("caseType", caseEntity.getCaseType().getCaseType());
        }
        if (caseEntity.getCasePriority() != null) {
            report.put("casePriority", caseEntity.getCasePriority().getCasePriority());
            report.put("casePriorityColour", caseEntity.getCasePriority().getColour());
        }

        report.put("taskCount", taskRepository.countByCaseEntityId(caseId));
        report.put("evidenceCount", evidenceRepository.countByCaseEntityId(caseId));

        List<Task> tasks = taskRepository.findByCaseEntityId(caseId);
        report.put("tasks", tasks.stream().map(t -> {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", t.getId());
            taskMap.put("taskName", t.getTaskName());
            taskMap.put("status", t.getCurrentStatus());
            taskMap.put("creationDate", t.getCreationDate());
            return taskMap;
        }).collect(Collectors.toList()));

        List<Evidence> evidenceList = evidenceRepository.findByCaseEntityId(caseId);
        report.put("evidence", evidenceList.stream().map(e -> {
            Map<String, Object> evMap = new HashMap<>();
            evMap.put("id", e.getId());
            evMap.put("reference", e.getReference());
            evMap.put("status", e.getCurrentStatus());
            evMap.put("dateAdded", e.getDateAdded());
            return evMap;
        }).collect(Collectors.toList()));

        return report;
    }

    public List<Map<String, Object>> getCaseActivityReport(Long caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        List<Map<String, Object>> activities = new ArrayList<>();

        for (CaseHistory hist : caseEntity.getHistory()) {
            Map<String, Object> activity = new LinkedHashMap<>();
            activity.put("id", hist.getId());
            activity.put("dateTime", hist.getDateTime());
            activity.put("userName", hist.getUser() != null ? hist.getUser().getFullName() : "System");
            activity.put("caseName", hist.getCaseName());
            activity.put("reference", hist.getReference());
            activities.add(activity);
        }

        for (CaseStatus status : caseEntity.getStatuses()) {
            Map<String, Object> activity = new LinkedHashMap<>();
            activity.put("id", status.getId());
            activity.put("dateTime", status.getDateTime());
            activity.put("userName", status.getUser() != null ? status.getUser().getFullName() : "System");
            activity.put("status", status.getStatus());
            activity.put("reason", status.getReason());
            activities.add(activity);
        }

        activities.sort((a, b) -> {
            LocalDateTime dtA = (LocalDateTime) a.get("dateTime");
            LocalDateTime dtB = (LocalDateTime) b.get("dateTime");
            if (dtA == null) return 1;
            if (dtB == null) return -1;
            return dtB.compareTo(dtA);
        });

        return activities;
    }
}
