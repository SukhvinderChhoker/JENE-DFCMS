package com.foreman.config;

import com.foreman.model.*;
import com.foreman.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ForemanOptionsRepository foremanOptionsRepository;

    @Autowired
    private CaseClassificationRepository caseClassificationRepository;

    @Autowired
    private CaseTypeRepository caseTypeRepository;

    @Autowired
    private CasePriorityRepository casePriorityRepository;

    @Autowired
    private TaskCategoryRepository taskCategoryRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    @Autowired
    private EvidenceTypeRepository evidenceTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            init();
        }
    }

    private void init() {
        Department dept = new Department();
        dept.setDepartment("Forensics");
        dept = departmentRepository.save(dept);

        Team team = new Team();
        team.setTeam("Digital Forensics");
        team.setDepartment(dept);
        team = teamRepository.save(team);

        ForemanOptions options = new ForemanOptions();
        options.setDateFormat("dd/MM/yyyy HH:mm");
        options.setDefaultLocation("Lab A");
        options.setCaseNames("CASE");
        options.setTaskNames("TASK");
        options.setCompany("JENE-DFCMS");
        options.setDepartment("Digital Forensics");
        options.setEvidenceRetention(true);
        options.setEvidenceRetentionPeriod(365);
        options.setNumberLoginsBeforeAccountLockout(5);
        foremanOptionsRepository.save(options);

        saveClassification("Public");
        saveClassification("Secret");
        saveClassification("Confidential");
        saveClassification("Internal");

        saveCaseType("eDiscovery");
        saveCaseType("Internal Investigation");
        saveCaseType("Fraud Investigation");
        saveCaseType("Incident Response");
        saveCaseType("Security & Malware Investigation");

        savePriority("Low", "#00CCFF", false);
        CasePriority normalPriority = savePriority("Normal", "#009900", true);
        savePriority("High", "#FF9933", false);
        savePriority("Critical", "#CC0000", false);

        saveEvidenceType("SATA Hard Drive");
        saveEvidenceType("IDE Hard Drive");
        saveEvidenceType("Mobile Phone");
        saveEvidenceType("Smart Phone");
        saveEvidenceType("Tablet");
        saveEvidenceType("USB Media");
        saveEvidenceType("Laptop");
        saveEvidenceType("Server");
        saveEvidenceType("CD");
        saveEvidenceType("DVD");

        TaskCategory commsCat = saveCategory("Communications Retrieval");
        TaskCategory internetCat = saveCategory("Internet Logs");
        TaskCategory computerCat = saveCategory("Computer Forensics");
        TaskCategory mobileCat = saveCategory("Mobile & Tablet Forensics");
        TaskCategory networkCat = saveCategory("Networked Data");
        TaskCategory logCat = saveCategory("Log file Analysis");
        TaskCategory specialistCat = saveCategory("Specialist Tasks");
        TaskCategory otherCat = saveCategory("Other");

        saveTaskType("Email Extraction", commsCat);
        saveTaskType("Chat Log Recovery", commsCat);
        saveTaskType("Browser History Analysis", internetCat);
        saveTaskType("Disk Forensics", computerCat);
        saveTaskType("Phone Extraction", mobileCat);
        saveTaskType("Network Capture Analysis", networkCat);
        saveTaskType("Server Log Review", logCat);
        saveTaskType("Malware Analysis", specialistCat);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setForename("Admin");
        admin.setSurname("User");
        admin.setEmail("admin@jene-dfcms.local");
        admin.setActive(true);
        admin.setValidated(true);
        admin.setTeam(team);
        admin = userRepository.save(admin);

        Role adminRole = new Role(admin, "ADMIN");
        admin.setRoles(List.of(adminRole));
        userRepository.save(admin);
    }

    private void saveClassification(String name) {
        CaseClassification cc = new CaseClassification();
        cc.setClassification(name);
        caseClassificationRepository.save(cc);
    }

    private void saveCaseType(String name) {
        CaseType ct = new CaseType();
        ct.setCaseType(name);
        caseTypeRepository.save(ct);
    }

    private CasePriority savePriority(String name, String colour, boolean isDefault) {
        CasePriority cp = new CasePriority();
        cp.setCasePriority(name);
        cp.setColour(colour);
        cp.setDefault(isDefault);
        return casePriorityRepository.save(cp);
    }

    private void saveEvidenceType(String name) {
        EvidenceType et = new EvidenceType();
        et.setEvidenceType(name);
        evidenceTypeRepository.save(et);
    }

    private TaskCategory saveCategory(String name) {
        TaskCategory tc = new TaskCategory();
        tc.setCategory(name);
        return taskCategoryRepository.save(tc);
    }

    private void saveTaskType(String name, TaskCategory category) {
        TaskType tt = new TaskType();
        tt.setTaskType(name);
        tt.setCategory(category);
        taskTypeRepository.save(tt);
    }
}
