package com.foreman.service;

import com.foreman.dto.UserDTO;
import com.foreman.model.Role;
import com.foreman.model.Team;
import com.foreman.model.User;
import com.foreman.repository.TeamRepository;
import com.foreman.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO createUser(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setForename(dto.getForename());
        user.setSurname(dto.getSurname());
        user.setMiddleName(dto.getMiddleName());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        user.setJobTitle(dto.getJobTitle());
        user.setActive(dto.isActive());

        if (dto.getTeam() != null && !dto.getTeam().isEmpty()) {
            teamRepository.findByTeam(dto.getTeam()).ifPresent(user::setTeam);
        }

        user = userRepository.save(user);

        if (dto.getRoles() != null) {
            List<Role> roles = new ArrayList<>();
            for (String roleName : dto.getRoles()) {
                Role role = new Role(user, roleName);
                roles.add(role);
            }
            user.setRoles(roles);
            userRepository.save(user);
        }

        return convertToDTO(user);
    }

    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setForename(dto.getForename());
        user.setSurname(dto.getSurname());
        user.setMiddleName(dto.getMiddleName());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        user.setJobTitle(dto.getJobTitle());
        user.setActive(dto.isActive());

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    public List<UserDTO> getUsersByRole(String role) {
        List<User> allUsers = userRepository.findAll();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : allUsers) {
            for (Role r : user.getRoles()) {
                if (r.getRole().equalsIgnoreCase(role) && !r.isRemoved()) {
                    filteredUsers.add(user);
                    break;
                }
            }
        }

        return filteredUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<UserDTO> getInvestigators() {
        return getUsersByRole("INVESTIGATOR");
    }

    public List<UserDTO> getCaseManagers() {
        return getUsersByRole("CASE_MANAGER");
    }

    public List<UserDTO> getQAs() {
        return getUsersByRole("QA");
    }

    public List<UserDTO> getAuthorisers() {
        return getUsersByRole("AUTHORISER");
    }

    public List<UserDTO> getRequesters() {
        return getUsersByRole("REQUESTER");
    }

    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setForename(user.getForename());
        dto.setSurname(user.getSurname());
        dto.setMiddleName(user.getMiddleName());
        dto.setEmail(user.getEmail());
        dto.setTelephone(user.getTelephone());
        dto.setJobTitle(user.getJobTitle());
        dto.setPhoto(user.getPhoto());
        dto.setActive(user.isActive());

        if (user.getTeam() != null) {
            dto.setTeam(user.getTeam().getTeam());
            if (user.getTeam().getDepartment() != null) {
                dto.setDepartment(user.getTeam().getDepartment().getDepartment());
            }
        }

        List<String> roles = user.getRoles().stream()
                .filter(r -> !r.isRemoved())
                .map(Role::getRole)
                .collect(Collectors.toList());
        dto.setRoles(roles);

        return dto;
    }
}
