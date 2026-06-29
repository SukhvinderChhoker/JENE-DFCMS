package com.foreman.config;

import com.foreman.model.*;
import com.foreman.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ForemanOptionsRepository foremanOptionsRepository;
    @Autowired private CaseClassificationRepository caseClassificationRepository;
    @Autowired private CaseTypeRepository caseTypeRepository;
    @Autowired private CasePriorityRepository casePriorityRepository;
    @Autowired private CaseRepository caseRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskCategoryRepository taskCategoryRepository;
    @Autowired private TaskTypeRepository taskTypeRepository;
    @Autowired private EvidenceRepository evidenceRepository;
    @Autowired private EvidenceTypeRepository evidenceTypeRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            init();
        }
    }

    private void init() {
        Department forensicsDept = createDepartment("Forensics");
        Department cyberDept = createDepartment("Cybercrime Division");
        Department opsDept = createDepartment("Operations");

        Team dfTeam = createTeam("Digital Forensics", forensicsDept);
        Team ccTeam = createTeam("Cybercrime Unit", cyberDept);
        Team intelTeam = createTeam("Intelligence", forensicsDept);
        Team opsTeam = createTeam("Field Operations", opsDept);
        Team mgmtTeam = createTeam("Management", opsDept);

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

        CaseClassification ccPublic = saveClassification("Public");
        CaseClassification ccSecret = saveClassification("Secret");
        CaseClassification ccConf = saveClassification("Confidential");
        CaseClassification ccInternal = saveClassification("Internal");

        CaseType ctDiscovery = saveCaseType("eDiscovery");
        CaseType ctInternal = saveCaseType("Internal Investigation");
        CaseType ctFraud = saveCaseType("Fraud Investigation");
        CaseType ctIncident = saveCaseType("Incident Response");
        CaseType ctMalware = saveCaseType("Security & Malware Investigation");

        CasePriority pLow = savePriority("Low", "#00CCFF", false);
        CasePriority pNormal = savePriority("Normal", "#009900", true);
        CasePriority pHigh = savePriority("High", "#FF9933", false);
        CasePriority pCritical = savePriority("Critical", "#CC0000", false);

        saveEvidenceType("PC");
        saveEvidenceType("Laptop");
        saveEvidenceType("Ext HDD/SSD");
        saveEvidenceType("Mobile");
        saveEvidenceType("Smart Device");
        saveEvidenceType("OM");

        TaskCategory commsCat = saveCategory("Communications Retrieval");
        TaskCategory internetCat = saveCategory("Internet Logs");
        TaskCategory computerCat = saveCategory("Computer Forensics");
        TaskCategory mobileCat = saveCategory("Mobile & Tablet Forensics");
        TaskCategory networkCat = saveCategory("Networked Data");
        TaskCategory logCat = saveCategory("Log file Analysis");
        TaskCategory specialistCat = saveCategory("Specialist Tasks");
        TaskCategory otherCat = saveCategory("Other");

        TaskType ttEmail = saveTaskType("Email Extraction", commsCat);
        TaskType ttChat = saveTaskType("Chat Log Recovery", commsCat);
        TaskType ttBrowser = saveTaskType("Browser History Analysis", internetCat);
        TaskType ttDisk = saveTaskType("Disk Forensics", computerCat);
        TaskType ttPhone = saveTaskType("Phone Extraction", mobileCat);
        TaskType ttNetwork = saveTaskType("Network Capture Analysis", networkCat);
        TaskType ttLog = saveTaskType("Server Log Review", logCat);
        TaskType ttMalware = saveTaskType("Malware Analysis", specialistCat);

        User admin = createUser("admin", "admin123", "Rajesh", "Kumar", "admin@jene-dfcms.local", dfTeam, "ADMIN");
        User cm1 = createUser("sarah.cm", "pass1234", "Sarah", "Mitchell", "sarah@jene-dfcms.local", dfTeam, "CASE_MANAGER");
        User inv1 = createUser("john.inv", "pass1234", "John", "Carter", "john@jene-dfcms.local", dfTeam, "INVESTIGATOR");
        User inv2 = createUser("priya.inv", "pass1234", "Priya", "Sharma", "priya@jene-dfcms.local", ccTeam, "INVESTIGATOR");
        User qa1 = createUser("mike.qa", "pass1234", "Mike", "Anderson", "mike@jene-dfcms.local", dfTeam, "QA");
        User req1 = createUser("lisa.req", "pass1234", "Lisa", "Brown", "lisa@jene-dfcms.local", opsTeam, "REQUESTER");
        User auth1 = createUser("david.auth", "pass1234", "David", "Wilson", "david@jene-dfcms.local", mgmtTeam, "AUTHORISER");
        User inv3 = createUser("ankit.inv", "pass1234", "Ankit", "Patel", "ankit@jene-dfcms.local", intelTeam, "INVESTIGATOR");

        EvidenceType smartDeviceType = evidenceTypeRepository.findByEvidenceType("Smart Device").orElse(null);
        EvidenceType mobileType = evidenceTypeRepository.findByEvidenceType("Mobile").orElse(null);
        EvidenceType laptopType = evidenceTypeRepository.findByEvidenceType("Laptop").orElse(null);
        EvidenceType pcType = evidenceTypeRepository.findByEvidenceType("PC").orElse(null);

        Case c1 = createCase("CF-2026-001", "Corporate Data Breach Investigation", "Unauthorized access detected in corporate network. Multiple employee credentials compromised. Server logs indicate lateral movement from initial entry point on June 15, 2026.", "HQ Building, Floor 3 - Server Room", ccConf, ctIncident, pCritical, admin);
        c1.setCocReceived(true);
        c1.setAgencyLetterReceived(true);
        c1.setAuthorityLetterReceived(true);
        c1.setEvidencePhotosReceived(true);
        c1.setStatus("OPEN", admin, "Case opened for investigation");
        caseRepository.save(c1);

        Case c2 = createCase("CF-2026-002", "Employee Email Fraud", "Suspected fraudulent email communications from finance department account. Multiple unauthorized wire transfer requests detected. Internal audit flagged suspicious patterns.", "Finance Department, Block B", ccInternal, ctFraud, pHigh, cm1);
        c2.setCocReceived(true);
        c2.setCaseHistoryReceived(true);
        c2.setStatus("OPEN", cm1, "Investigation assigned to team");
        caseRepository.save(c2);

        Case c3 = createCase("CF-2026-003", "Ransomware Incident - Marketing Server", "Marketing server encrypted by ransomware. Initial analysis suggests phishing email as entry point. Need to identify scope of data exfiltration.", "Marketing Dept, Building 2", ccSecret, ctMalware, pCritical, cm1);
        c3.setCocReceived(true);
        c3.setAgencyLetterReceived(true);
        c3.setAuthorityLetterReceived(true);
        c3.setConsentFormReceived(true);
        c3.setEvidencePhotosReceived(true);
        c3.setSeizureMemoReceived(true);
        c3.setStatus("OPEN", admin, "Critical - immediate response required");
        caseRepository.save(c3);

        Case c4 = createCase("CF-2026-004", "Lost Mobile Device Investigation", "Company-issued smartphone lost by employee during business trip. Device contains sensitive client data. Need to assess data exposure risk.", "Hotel Grand, Room 412", ccConf, ctIncident, pNormal, req1);
        c4.setCocReceived(true);
        c4.setStatus("PENDING", cm1, "Awaiting device location confirmation");
        caseRepository.save(c4);

        Case c5 = createCase("CF-2026-005", "Insider Threat - Data Exfiltration", "Anomalous data transfer patterns detected from engineering department workstation. USB devices connected outside policy hours. 2.3GB of proprietary source code transferred.", "Engineering Wing, Workstation 47", ccSecret, ctInternal, pHigh, admin);
        c5.setCocReceived(true);
        c5.setAgencyLetterReceived(true);
        c5.setAuthorityLetterReceived(true);
        c5.setWitnessStatementReceived(true);
        c5.setStatus("OPEN", admin, "Under active investigation");
        caseRepository.save(c5);

        Case c6 = createCase("CF-2026-006", "DDoS Attack Analysis", "Distributed denial of service attack targeted public-facing web application on June 20, 2026. Attack lasted 4 hours causing service disruption. Need to identify attack vectors and origin.", "Cloud Infrastructure - AWS Region", ccPublic, ctIncident, pNormal, inv1);
        c6.setStatus("CREATED", inv1, "Initial report filed");
        caseRepository.save(c6);

        createTask("Email Log Extraction", ttEmail, c1, inv1, "PROGRESS", "Extract all email logs from June 1-15 for compromised accounts");
        createTask("Browser History Recovery", ttBrowser, c1, inv2, "COMPLETE", "Recovered Chrome and Firefox histories from 3 affected workstations");
        createTask("Disk Forensics - Server", ttDisk, c1, inv1, "ALLOCATED", "Full forensic image of compromised server disk");
        createTask("Network Traffic Analysis", ttNetwork, c1, inv3, "QUEUED", "Analyze pcap captures from June 14-16 for C2 communications");

        createTask("Email Fraud Analysis", ttEmail, c2, inv2, "PROGRESS", "Review all outgoing emails from finance@company.com in May-June");
        createTask("Chat Log Recovery", ttChat, c2, inv2, "CREATED", "Extract Slack and Teams messages from finance team");

        createTask("Malware Sample Analysis", ttMalware, c3, inv1, "PROGRESS", "Analyze ransomware binary for IOCs and encryption method");
        createTask("Server Log Review", ttLog, c3, inv3, "ALLOCATED", "Review IIS and Windows Event logs for initial compromise");
        createTask("Browser History Analysis", ttBrowser, c3, inv2, "QUEUED", "Check for phishing email source and user interactions");
        createTask("Disk Forensics - Workstation", ttDisk, c3, inv1, "CREATED", "Forensic image of patient zero workstation");

        createTask("Phone Extraction", ttPhone, c4, inv2, "CREATED", "Attempt remote wipe and data extraction if device located");

        createTask("USB Device Analysis", ttDisk, c5, inv1, "PROGRESS", "Analyze USB connection logs and file transfer artifacts");
        createTask("Network Capture Review", ttNetwork, c5, inv3, "ALLOCATED", "Review network logs for data exfiltration patterns");

        createTask("DDoS Log Analysis", ttLog, c6, inv3, "CREATED", "Analyze CloudFlare and AWS WAF logs");
        createTask("Network Forensics", ttNetwork, c6, inv1, "QUEUED", "Packet capture analysis for attack signatures");

        createEvidence("Smart Device", smartDeviceType, c1, inv1, "Samsung Galaxy S24 belonging to IT admin. Found in desk drawer.", "EVD-001", "Evidence Locker A", "ACTIVE");
        createEvidence("Ext HDD/SSD", pcType, c1, inv2, "SanDisk 64GB USB drive. Found connected to compromised server.", "EVD-002", "Evidence Locker A", "ACTIVE");
        createEvidence("Laptop", laptopType, c2, inv2, "Dell Latitude laptop from finance department. Contains email client data.", "EVD-003", "Evidence Locker B", "ACTIVE");
        createEvidence("PC", pcType, c3, inv1, "Dell PowerEdge R740 - Marketing server. Drives removed for forensic imaging.", "EVD-004", "Evidence Locker A", "ACTIVE");
        createEvidence("Mobile", mobileType, c4, inv2, "iPhone 15 Pro - company issued. Location data shows last active in hotel.", "EVD-005", "Evidence Locker B", "INACTIVE");
        createEvidence("Ext HDD/SSD", pcType, c5, inv1, "Kingston 128GB USB drive. Recovered from engineering workstation.", "EVD-006", "Evidence Locker A", "ACTIVE");
        createEvidence("Laptop", laptopType, c5, inv3, "ThinkPad X1 Carbon. Secondary device used for data staging.", "EVD-007", "Evidence Locker B", "ACTIVE");

        System.out.println("=== JENE-DFCMS Seed Data Loaded ===");
        System.out.println("Users: admin/admin123, sarah.cm/pass1234, john.inv/pass1234, priya.inv/pass1234");
        System.out.println("       mike.qa/pass1234, lisa.req/pass1234, david.auth/pass1234, ankit.inv/pass1234");
        System.out.println("Cases: 6 cases with tasks and evidence");
        System.out.println("====================================");
    }

    private Department createDepartment(String name) {
        Department dept = new Department();
        dept.setDepartment(name);
        return departmentRepository.save(dept);
    }

    private Team createTeam(String name, Department dept) {
        Team team = new Team();
        team.setTeam(name);
        team.setDepartment(dept);
        return teamRepository.save(team);
    }

    private CaseClassification saveClassification(String name) {
        CaseClassification cc = new CaseClassification();
        cc.setClassification(name);
        return caseClassificationRepository.save(cc);
    }

    private CaseType saveCaseType(String name) {
        CaseType ct = new CaseType();
        ct.setCaseType(name);
        return caseTypeRepository.save(ct);
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

    private TaskType saveTaskType(String name, TaskCategory category) {
        TaskType tt = new TaskType();
        tt.setTaskType(name);
        tt.setCategory(category);
        return taskTypeRepository.save(tt);
    }

    private User createUser(String username, String password, String forename, String surname, String email, Team team, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setForename(forename);
        user.setSurname(surname);
        user.setEmail(email);
        user.setActive(true);
        user.setValidated(true);
        user.setTeam(team);
        user = userRepository.save(user);
        Role role = new Role(user, roleName);
        user.setRoles(List.of(role));
        return userRepository.save(user);
    }

    private Case createCase(String reference, String name, String background, String location,
                           CaseClassification classification, CaseType caseType,
                           CasePriority priority, User creator) {
        Case c = new Case();
        c.setReference(reference);
        c.setCaseName(name);
        c.setBackground(background);
        c.setLocation(location);
        c.setClassification(classification);
        c.setCaseType(caseType);
        c.setCasePriority(priority);
        c.setCasePriorityColour(priority.getColour());
        c.setDeadline(LocalDateTime.now().plusDays(30));
        c = caseRepository.save(c);
        c.addChange(creator);
        return caseRepository.save(c);
    }

    private void createTask(String name, TaskType type, Case caseEntity, User assignee, String status, String background) {
        Task t = new Task();
        t.setTaskName(name);
        t.setTaskType(type);
        t.setCaseEntity(caseEntity);
        t.setBackground(background);
        t.setCurrentStatus(status);
        t.setLocation(caseEntity.getLocation());
        t.setDeadline(LocalDateTime.now().plusDays(14));
        t = taskRepository.save(t);

        TaskStatus ts = new TaskStatus();
        ts.setTask(t);
        ts.setCaseEntity(caseEntity);
        ts.setStatus(status);
        ts.setDateTime(LocalDateTime.now());
        ts.setUser(assignee);
        t.getStatuses().add(ts);
        taskRepository.save(t);
    }

    private void createEvidence(String name, EvidenceType type, Case caseEntity, User user,
                               String comment, String reference, String location, String status) {
        Evidence e = new Evidence();
        e.setReference(reference);
        e.setType(type);
        e.setCaseEntity(caseEntity);
        e.setUser(user);
        e.setComment(comment);
        e.setOriginator(user.getFullName());
        e.setEvidenceBagNumber("BAG-" + reference.replace("EVD-", ""));
        e.setLocation(location);
        e.setCurrentStatus(status);
        e.setDateAdded(LocalDateTime.now());
        e = evidenceRepository.save(e);

        EvidenceStatus es = new EvidenceStatus();
        es.setEvidence(e);
        es.setStatus(status);
        es.setDateTime(LocalDateTime.now());
        es.setUser(user);
        e.getStatuses().add(es);
        evidenceRepository.save(e);
    }
}
