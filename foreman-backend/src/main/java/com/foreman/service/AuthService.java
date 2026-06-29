package com.foreman.service;

import com.foreman.dto.LoginRequest;
import com.foreman.dto.LoginResponse;
import com.foreman.dto.UserDTO;
import com.foreman.model.Role;
import com.foreman.model.User;
import com.foreman.repository.UserRepository;
import com.foreman.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles().stream()
                .filter(r -> !r.isRemoved())
                .map(Role::getRole)
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getUsername(), roles);

        return new LoginResponse(token, user.getUsername(), roles, user.getId());
    }

    public UserDTO register(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode("password123"));
        user.setForename(dto.getForename());
        user.setSurname(dto.getSurname());
        user.setMiddleName(dto.getMiddleName());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        user.setJobTitle(dto.getJobTitle());
        user.setActive(true);

        user = userRepository.save(user);

        Role role = new Role(user, "INVESTIGATOR");
        user.setRoles(List.of(role));
        userRepository.save(user);

        return convertToDTO(user);
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
