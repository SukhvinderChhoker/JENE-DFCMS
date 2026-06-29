package com.foreman.service;

import com.foreman.dto.TaskDTO;
import com.foreman.model.*;
import com.foreman.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    @Autowired
    private TaskNotesRepository taskNotesRepository;

    @Autowired
    private UserTaskRoleRepository userTaskRoleRepository;

    @Autowired
    private ForemanOptionsRepository foremanOptionsRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<TaskDTO> getTasksByCase(Long caseId) {
        List<Task> tasks = taskRepository.findByCaseEntityId(caseId);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return convertToDTO(task);
    }

    @Transactional
    public TaskDTO createTask(Long caseId, TaskDTO dto, Long userId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ForemanOptions options = foremanOptionsRepository.findFirstByOrderByIdAsc().orElse(new ForemanOptions());

        Task task = new Task();
        task.setTaskName(dto.getTaskName());
        task.setCaseEntity(caseEntity);

        long count = taskRepository.count() + 1;
        String prefix = options.getTaskNames() != null ? options.getTaskNames() : "TASK";
        task.setBackground(dto.getBackground());
        task.setLocation(dto.getLocation());
        task.setCreationDate(LocalDateTime.now());

        if (dto.getTaskType() != null) {
            taskTypeRepository.findByTaskType(dto.getTaskType())
                    .ifPresent(task::setTaskType);
        }

        if (dto.getDeadline() != null) {
            task.setDeadline(LocalDateTime.parse(dto.getDeadline(), FORMATTER));
        }

        task.setCurrentStatus(TaskStatus.CREATED);

        TaskStatus status = new TaskStatus();
        status.setTask(task);
        status.setCaseEntity(caseEntity);
        status.setStatus(TaskStatus.CREATED);
        status.setDateTime(LocalDateTime.now());
        status.setUser(user);
        task.getStatuses().add(status);

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO dto, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setTaskName(dto.getTaskName());
        task.setBackground(dto.getBackground());
        task.setLocation(dto.getLocation());

        if (dto.getTaskType() != null) {
            taskTypeRepository.findByTaskType(dto.getTaskType())
                    .ifPresent(task::setTaskType);
        }

        if (dto.getDeadline() != null) {
            task.setDeadline(LocalDateTime.parse(dto.getDeadline(), FORMATTER));
        }

        TaskHistory hist = new TaskHistory();
        hist.setTask(task);
        hist.setUser(user);
        hist.setDateTime(LocalDateTime.now());
        hist.setTaskName(task.getTaskName());
        hist.setTaskType(task.getTaskType());
        hist.setBackground(task.getBackground());
        hist.setLocation(task.getLocation());
        hist.setDeadline(task.getDeadline());
        hist.setCaseEntity(task.getCaseEntity());
        task.getHistory().add(hist);

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Transactional
    public TaskDTO changeStatus(Long id, String status, String note, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setCurrentStatus(status);

        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTask(task);
        taskStatus.setCaseEntity(task.getCaseEntity());
        taskStatus.setStatus(status);
        taskStatus.setDateTime(LocalDateTime.now());
        taskStatus.setUser(user);
        taskStatus.setNote(note);
        task.getStatuses().add(taskStatus);

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Transactional
    public void assignInvestigator(Long taskId, Long userId, boolean principle, Long assignerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Investigator not found"));

        String role = principle ? UserTaskRole.PRINCIPLE_INVESTIGATOR : UserTaskRole.SECONDARY_INVESTIGATOR;

        UserTaskRole existing = userTaskRoleRepository.findByTaskIdAndUserIdAndRole(taskId, userId, role);
        if (existing == null) {
            // Remove existing role if switching
            List<UserTaskRole> existingRoles = userTaskRoleRepository.findByTaskId(taskId);
            for (UserTaskRole u : existingRoles) {
                if (u.getUser().getId().equals(userId)) {
                    userTaskRoleRepository.delete(u);
                }
            }

            UserTaskRole taskRole = new UserTaskRole(user, task, role);
            userTaskRoleRepository.save(taskRole);
        }
    }

    @Transactional
    public void assignQA(Long taskId, Long userId, boolean principle, Long assignerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("QA user not found"));

        String role = principle ? UserTaskRole.PRINCIPLE_QA : UserTaskRole.SECONDARY_QA;

        UserTaskRole existing = userTaskRoleRepository.findByTaskIdAndUserIdAndRole(taskId, userId, role);
        if (existing == null) {
            List<UserTaskRole> existingRoles = userTaskRoleRepository.findByTaskId(taskId);
            for (UserTaskRole u : existingRoles) {
                if (u.getUser().getId().equals(userId) && u.getRole().equals(role)) {
                    userTaskRoleRepository.delete(u);
                }
            }

            UserTaskRole taskRole = new UserTaskRole(user, task, role);
            userTaskRoleRepository.save(taskRole);
        }
    }

    @Transactional
    public TaskNotes addNote(Long taskId, String note, Long authorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        TaskNotes taskNote = new TaskNotes();
        taskNote.setTask(task);
        taskNote.setAuthor(author);
        taskNote.setNote(note);
        taskNote.setDateTime(LocalDateTime.now());

        return taskNotesRepository.save(taskNote);
    }

    public List<TaskNotes> getTaskNotes(Long taskId) {
        return taskNotesRepository.findByTaskIdOrderByDateTimeDesc(taskId);
    }

    public List<TaskHistory> getTaskHistory(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return task.getHistory();
    }

    public List<FileUpload> getTaskUploads(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return fileUploadRepository.findByTaskIdAndDeletedFalse(taskId);
    }

    public TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTaskName(task.getTaskName());
        dto.setCaseId(task.getCaseEntity().getId());
        dto.setCaseName(task.getCaseEntity().getCaseName());
        dto.setBackground(task.getBackground());
        dto.setCurrentStatus(task.getCurrentStatus());
        dto.setLocation(task.getLocation());

        if (task.getTaskType() != null) {
            dto.setTaskType(task.getTaskType().getTaskType());
        }

        if (task.getCreationDate() != null) {
            dto.setCreationDate(task.getCreationDate().format(FORMATTER));
        }

        if (task.getDeadline() != null) {
            dto.setDeadline(task.getDeadline().format(FORMATTER));
        }

        List<UserTaskRole> taskRoles = userTaskRoleRepository.findByTaskId(task.getId());
        for (UserTaskRole utr : taskRoles) {
            String fullname = utr.getUser().getFullName();
            switch (utr.getRole()) {
                case UserTaskRole.PRINCIPLE_INVESTIGATOR -> dto.setPrincipleInvestigator(fullname);
                case UserTaskRole.SECONDARY_INVESTIGATOR -> dto.setSecondaryInvestigator(fullname);
                case UserTaskRole.PRINCIPLE_QA -> dto.setPrincipleQA(fullname);
                case UserTaskRole.SECONDARY_QA -> dto.setSecondaryQA(fullname);
            }
        }

        return dto;
    }
}
